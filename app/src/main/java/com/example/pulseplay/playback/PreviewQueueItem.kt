package com.example.pulseplay.playback

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.example.pulseplay.data.model.Track

data class PreviewQueueItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val artworkUrl: String?,
    val previewUrl: String,
)

fun Track.toPreviewQueueItem(): PreviewQueueItem? {
    val url = preview_url ?: return null
    return PreviewQueueItem(
        id = id,
        title = name,
        subtitle = artists.joinToString { it.name },
        artworkUrl = album?.images?.firstOrNull()?.url,
        previewUrl = url,
    )
}

fun PreviewQueueItem.toMediaItem(): MediaItem =
    MediaItem.Builder()
        .setMediaId(id)
        .setUri(previewUrl)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setArtist(subtitle)
                .setArtworkUri(artworkUrl?.let { Uri.parse(it) })
                .build(),
        )
        .build()
