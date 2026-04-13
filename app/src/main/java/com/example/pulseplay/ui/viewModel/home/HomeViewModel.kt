package com.example.pulseplay.ui.viewModel.home

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulseplay.data.model.Album
import com.example.pulseplay.data.model.Artist
import com.example.pulseplay.data.model.AudioBook
import com.example.pulseplay.data.model.Episode
import com.example.pulseplay.data.model.Playlist
import com.example.pulseplay.data.model.Show
import com.example.pulseplay.data.model.Track
import com.example.pulseplay.data.repository.PulsePlayRepository
import com.example.pulseplay.utils.PreferenceUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private var repository: PulsePlayRepository,
    private var context: Application,
) : ViewModel() {

    val TAG = "HomeViewModel"

    var artists by mutableStateOf<List<Artist>>(emptyList())
        private set

    var tracks by mutableStateOf<List<Track>>(emptyList())
        private set

    var albums by mutableStateOf<List<Album>>(emptyList())
        private set

    var playlists by mutableStateOf<List<Playlist>>(emptyList())
        private set

    var shows by mutableStateOf<List<Show>>(emptyList())
        private set

    var episodes by mutableStateOf<List<Episode>>(emptyList())
        private set

    var audiobooks by mutableStateOf<List<AudioBook>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    private val _navigateToLogOutPopUp = MutableSharedFlow<Unit>()
    val navigateToLogOutPopUp: SharedFlow<Unit> = _navigateToLogOutPopUp

    /**
     * Spotify [GET /v1/search](https://developer.spotify.com/documentation/web-api/reference/search)
     * with multiple types (see [PulsePlayRepository.searchCatalog]).
     */
    fun search(query: String) {
        val token = PreferenceUtils.getData(context, PreferenceUtils.accessToken)
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.searchCatalog(
                    query,
                    "Bearer $token",
                    market = "IN"
                )
                Log.d(TAG, "response ${response.artists}")
                artists = response.artists?.items.orEmpty()
                tracks = response.tracks?.items.orEmpty().filter { it.preview_url != null }
                albums = response.albums?.items.orEmpty()
                playlists = response.playlists?.items.orEmpty()
                shows = response.shows?.items.orEmpty()
                episodes = response.episodes?.items.orEmpty()
                audiobooks = response.audiobooks?.items.orEmpty()
            } catch (e: HttpException) {
                when (e.code()) {
                    400 -> {
                        _navigateToLogOutPopUp.emit(Unit)
                        Log.e(TAG, "HTTP 400: Bad Request - Invalid code or verifier.")
                    }

                    401 -> {
                        Log.e(TAG, "HTTP 401: Unauthorized - Invalid credentials.")
                        PreferenceUtils.setBoolData(context, PreferenceUtils.loginStatus, false)
                        _navigateToLogOutPopUp.emit(Unit)
                    }

                    else -> {
                        Log.e(TAG, "Error body: ${e.response()?.errorBody()?.string()}")
                        Log.e(TAG, "HTTP ${e.code()} Code : ${e.message}")
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, "Network error: ${e.localizedMessage}")
            }
            isLoading = false
        }
    }

    fun clearSearchResults() {
        artists = emptyList()
        tracks = emptyList()
        albums = emptyList()
        playlists = emptyList()
        shows = emptyList()
        episodes = emptyList()
        audiobooks = emptyList()
    }
}
