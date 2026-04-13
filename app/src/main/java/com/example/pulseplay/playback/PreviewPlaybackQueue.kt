package com.example.pulseplay.playback

import androidx.media3.common.MediaItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreviewPlaybackQueue @Inject constructor() {
    private var items: List<PreviewQueueItem> = emptyList()
    var startIndex: Int = 0
        private set

    var progressHint: String = "30s preview"
        private set

    fun setQueue(
        items: List<PreviewQueueItem>,
        startIndex: Int,
        progressHint: String = "30s preview",
    ) {
        this.items = items
        this.startIndex = startIndex.coerceIn(0, (items.size - 1).coerceAtLeast(0))
        this.progressHint = progressHint
    }

    fun toMediaItems(): List<MediaItem> = items.map { it.toMediaItem() }

    fun itemAt(index: Int): PreviewQueueItem? = items.getOrNull(index)

    fun snapshot(): List<PreviewQueueItem> = items.toList()
}
