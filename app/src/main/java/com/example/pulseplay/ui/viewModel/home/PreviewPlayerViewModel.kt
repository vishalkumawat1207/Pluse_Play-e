package com.example.pulseplay.ui.viewModel.home

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.pulseplay.playback.LocalPlaybackSample
import com.example.pulseplay.playback.PreviewPlaybackQueue
import com.example.pulseplay.playback.PreviewPlaybackService
import com.example.pulseplay.playback.PreviewPlayerStateRepository
import com.example.pulseplay.playback.PreviewPlayerUiState
import com.example.pulseplay.playback.PreviewQueueItem
import com.example.pulseplay.playback.PlaybackActions
import com.example.pulseplay.playback.toPreviewQueueItem
import com.example.pulseplay.data.model.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PreviewPlayerViewModel @Inject constructor(
    @ApplicationContext private val app: Context,
    private val queueHolder: PreviewPlaybackQueue,
    private val stateRepository: PreviewPlayerStateRepository,
) : ViewModel() {

    val uiState: StateFlow<PreviewPlayerUiState> = stateRepository.state

    fun playTrack(track: Track, allTracksInSection: List<Track>) {
        if (track.preview_url == null) {
            Toast.makeText(app, "No 30s preview for this track", Toast.LENGTH_SHORT).show()
            return
        }
        val withPreview = allTracksInSection.mapNotNull { it.toPreviewQueueItem() }
        if (withPreview.isEmpty()) {
            Toast.makeText(app, "No previews in these results", Toast.LENGTH_SHORT).show()
            return
        }
        val startIndex = withPreview.indexOfFirst { it.id == track.id }.takeIf { it >= 0 } ?: 0
        queueHolder.setQueue(withPreview, startIndex, "30s preview")
        val intent = Intent(app, PreviewPlaybackService::class.java).apply {
            action = PlaybackActions.LOAD_AND_PLAY
        }
        ContextCompat.startForegroundService(app, intent)
    }

    /**
     * Plays bundled demo clips (`demo_track_1` … `demo_track_10`): each session shuffles the pool,
     * then each list row gets the next URI (cycles for long lists). Metadata comes from Spotify.
     */
    fun playTracksWithLocalSample(tracks: List<Track>, startIndex: Int, artworkFallback: String?) {
        if (tracks.isEmpty()) {
            Toast.makeText(app, "No tracks to play", Toast.LENGTH_SHORT).show()
            return
        }
        val uriPool = LocalPlaybackSample.shuffledDemoUris(app)
        val items = tracks.mapIndexed { i, t ->
            PreviewQueueItem(
                id = t.id,
                title = t.name,
                subtitle = t.artists.joinToString { it.name },
                artworkUrl = t.album?.images?.firstOrNull()?.url ?: artworkFallback,
                previewUrl = uriPool[i % uriPool.size],
            )
        }
        val start = startIndex.coerceIn(0, items.lastIndex)
        queueHolder.setQueue(items, start, "Sample audio")
        val intent = Intent(app, PreviewPlaybackService::class.java).apply {
            action = PlaybackActions.LOAD_AND_PLAY
        }
        ContextCompat.startForegroundService(app, intent)
    }

    fun togglePlayPause() {
        val intent = Intent(app, PreviewPlaybackService::class.java).apply {
            action = PlaybackActions.TOGGLE
        }
        app.startService(intent)
    }

    fun skipNext() {
        val intent = Intent(app, PreviewPlaybackService::class.java).apply {
            action = PlaybackActions.NEXT
        }
        app.startService(intent)
    }

    fun skipPrevious() {
        val intent = Intent(app, PreviewPlaybackService::class.java).apply {
            action = PlaybackActions.PREV
        }
        app.startService(intent)
    }

    fun seekTo(positionMs: Long) {
        val intent = Intent(app, PreviewPlaybackService::class.java).apply {
            action = PlaybackActions.SEEK
            putExtra(PlaybackActions.EXTRA_POSITION_MS, positionMs)
        }
        app.startService(intent)
    }

    fun seekToQueueIndex(index: Int) {
        val intent = Intent(app, PreviewPlaybackService::class.java).apply {
            action = PlaybackActions.SEEK_TO_QUEUE_INDEX
            putExtra(PlaybackActions.EXTRA_QUEUE_INDEX, index)
        }
        app.startService(intent)
    }

    fun toggleShuffle() {
        val intent = Intent(app, PreviewPlaybackService::class.java).apply {
            action = PlaybackActions.TOGGLE_SHUFFLE
        }
        app.startService(intent)
    }

    fun cycleRepeatMode() {
        val intent = Intent(app, PreviewPlaybackService::class.java).apply {
            action = PlaybackActions.CYCLE_REPEAT
        }
        app.startService(intent)
    }

    fun queueSnapshot(): List<PreviewQueueItem> = queueHolder.snapshot()
}
