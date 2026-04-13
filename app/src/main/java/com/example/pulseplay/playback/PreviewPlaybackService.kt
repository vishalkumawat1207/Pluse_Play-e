package com.example.pulseplay.playback

import android.app.PendingIntent
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.core.app.ServiceCompat
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.pulseplay.MainActivity
import com.example.pulseplay.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PreviewPlaybackService : MediaSessionService() {

    @Inject lateinit var queueHolder: PreviewPlaybackQueue
    @Inject lateinit var stateRepository: PreviewPlayerStateRepository

    private var mediaSession: MediaSession? = null
    private val handler = Handler(Looper.getMainLooper())
    private var positionRunnable: Runnable? = null

    override fun onCreate() {
        super.onCreate()
        setMediaNotificationProvider(
            DefaultMediaNotificationProvider.Builder(this)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .setChannelName(R.string.notification_channel_playback)
                .build(),
        )

        val player = ExoPlayer.Builder(this).build().apply {
            repeatMode = Player.REPEAT_MODE_OFF
        }
        player.addListener(object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                if (events.containsAny(
                        Player.EVENT_PLAYBACK_STATE_CHANGED,
                        Player.EVENT_MEDIA_ITEM_TRANSITION,
                        Player.EVENT_IS_PLAYING_CHANGED,
                        Player.EVENT_SHUFFLE_MODE_ENABLED_CHANGED,
                        Player.EVENT_REPEAT_MODE_CHANGED,
                    )
                ) {
                    syncStateFromPlayer(player)
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) schedulePositionUpdates() else stopPositionUpdates()
                mediaSession?.player?.let { syncStateFromPlayer(it) }
            }
        })

        val sessionActivity = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

        mediaSession = MediaSession.Builder(this, player)
            .setId("pulseplay_session")
            .setSessionActivity(sessionActivity)
            .build()

        // Required when playback is started via startForegroundService (not only bind): registers
        // the session so Media3 shows the media notification and wires system transport controls.
        addSession(mediaSession!!)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val player = mediaSession?.player ?: return super.onStartCommand(intent, flags, startId)
        when (intent?.action) {
            PlaybackActions.LOAD_AND_PLAY -> {
                val items = queueHolder.toMediaItems()
                if (items.isEmpty()) {
                    stateRepository.clear()
                    return super.onStartCommand(intent, flags, startId)
                }
                val startIdx = queueHolder.startIndex.coerceIn(0, items.lastIndex)
                player.stop()
                player.clearMediaItems()
                player.setMediaItems(items, startIdx, C.TIME_UNSET)
                player.prepare()
                player.play()
                syncStateFromPlayer(player)
            }
            PlaybackActions.TOGGLE -> {
                if (player.isPlaying) player.pause() else player.play()
                syncStateFromPlayer(player)
            }
            PlaybackActions.NEXT -> {
                if (player.hasNextMediaItem()) player.seekToNextMediaItem()
                syncStateFromPlayer(player)
            }
            PlaybackActions.PREV -> {
                if (player.hasPreviousMediaItem()) player.seekToPreviousMediaItem()
                syncStateFromPlayer(player)
            }
            PlaybackActions.SEEK -> {
                val pos = intent.getLongExtra(PlaybackActions.EXTRA_POSITION_MS, -1L)
                if (pos >= 0L) player.seekTo(pos)
                syncStateFromPlayer(player)
            }
            PlaybackActions.SEEK_TO_QUEUE_INDEX -> {
                val idx = intent.getIntExtra(PlaybackActions.EXTRA_QUEUE_INDEX, -1)
                if (idx >= 0 && idx < player.mediaItemCount) {
                    player.seekTo(idx, C.TIME_UNSET)
                }
                syncStateFromPlayer(player)
            }
            PlaybackActions.TOGGLE_SHUFFLE -> {
                player.shuffleModeEnabled = !player.shuffleModeEnabled
                syncStateFromPlayer(player)
            }
            PlaybackActions.CYCLE_REPEAT -> {
                player.repeatMode = when (player.repeatMode) {
                    Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
                    Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
                    else -> Player.REPEAT_MODE_OFF
                }
                syncStateFromPlayer(player)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopPositionUpdates()
        mediaSession?.player?.let { p ->
            p.stop()
            p.clearMediaItems()
        }
        stateRepository.clear()
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun schedulePositionUpdates() {
        stopPositionUpdates()
        positionRunnable = object : Runnable {
            override fun run() {
                mediaSession?.player?.let { syncStateFromPlayer(it) }
                if (mediaSession?.player?.isPlaying == true) {
                    handler.postDelayed(this, 450L)
                }
            }
        }
        handler.post(positionRunnable!!)
    }

    private fun stopPositionUpdates() {
        positionRunnable?.let { handler.removeCallbacks(it) }
        positionRunnable = null
    }

    private fun syncStateFromPlayer(player: Player) {
        val index = player.currentMediaItemIndex
        val item = queueHolder.itemAt(index)
        val dur = player.duration
        val durationMs = when {
            dur == C.TIME_UNSET || dur < 0 -> 0L
            else -> dur
        }
        stateRepository.update(
            PreviewPlayerUiState(
                isPlaying = player.isPlaying,
                currentTitle = item?.title
                    ?: player.mediaMetadata.title?.toString().orEmpty(),
                currentSubtitle = item?.subtitle
                    ?: player.mediaMetadata.artist?.toString().orEmpty(),
                artworkUrl = item?.artworkUrl,
                positionMs = player.currentPosition.coerceAtLeast(0L),
                durationMs = durationMs,
                currentIndex = index,
                queueSize = player.mediaItemCount,
                hasActiveSession = player.mediaItemCount > 0,
                progressHint = queueHolder.progressHint,
                shuffleEnabled = player.shuffleModeEnabled,
                repeatMode = player.repeatMode,
            ),
        )
    }

    override fun onDestroy() {
        stopPositionUpdates()
        stateRepository.clear()
        mediaSession?.release()
        mediaSession = null
        super.onDestroy()
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "pulseplay_playback"
    }
}
