package com.example.pulseplay.ui.viewModel.detail

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulseplay.data.model.Track
import com.example.pulseplay.data.repository.PulsePlayRepository
import com.example.pulseplay.utils.PreferenceUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
    private val repository: PulsePlayRepository,
    private val app: Application,
) : ViewModel() {

    var title by mutableStateOf("")
        private set
    var subtitle by mutableStateOf<String?>(null)
        private set
    var imageUrl by mutableStateOf<String?>(null)
        private set
    var tracks by mutableStateOf<List<Track>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    fun load(playlistId: String) {
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                val token = PreferenceUtils.getData(app, PreferenceUtils.accessToken)
                val auth = "Bearer $token"
                val playlist = repository.getPlaylist(playlistId, auth)
                title = playlist.name
                subtitle = playlist.owner.display_name?.takeIf { it.isNotBlank() }
                imageUrl = playlist.images?.firstOrNull()?.url
                val page = repository.getPlaylistTracks(playlistId, auth)
                tracks = page.items.mapNotNull { it.track }
            } catch (e: HttpException) {
                error = e.message ?: "HTTP ${e.code()}"
            } catch (e: IOException) {
                error = e.message ?: "Network error"
            } catch (e: Exception) {
                error = e.message ?: "Failed to load"
            } finally {
                isLoading = false
            }
        }
    }
}
