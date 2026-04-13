package com.example.pulseplay.playback

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class PreviewPlayerUiState(
    val isPlaying: Boolean = false,
    val currentTitle: String = "",
    val currentSubtitle: String = "",
    val artworkUrl: String? = null,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val currentIndex: Int = 0,
    val queueSize: Int = 0,
    val hasActiveSession: Boolean = false,
    /** Shown in mini-player next to the timeline (e.g. "30s preview" vs bundled sample). */
    val progressHint: String = "30s preview",
    val shuffleEnabled: Boolean = false,
    /** [androidx.media3.common.Player] repeat mode: REPEAT_MODE_OFF, ALL, or ONE. */
    val repeatMode: Int = 0,
)

@Singleton
class PreviewPlayerStateRepository @Inject constructor() {
    private val _state = MutableStateFlow(PreviewPlayerUiState())
    val state: StateFlow<PreviewPlayerUiState> = _state.asStateFlow()

    fun update(value: PreviewPlayerUiState) {
        _state.value = value
    }

    fun clear() {
        _state.value = PreviewPlayerUiState()
    }
}
