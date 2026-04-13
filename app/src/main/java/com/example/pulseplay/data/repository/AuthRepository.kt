package com.example.pulseplay.data.repository

import com.example.pulseplay.data.apiService.ApiService
import com.example.pulseplay.data.apiService.AuthApiService
import com.example.pulseplay.data.model.Album
import com.example.pulseplay.data.model.AlbumSearchResponse
import com.example.pulseplay.data.model.AlbumTracksPage
import com.example.pulseplay.data.model.Artist
import com.example.pulseplay.data.model.ArtistResponse
import com.example.pulseplay.data.model.ArtistTopTracksResponse
import com.example.pulseplay.data.model.AudiobookSearchResponse
import com.example.pulseplay.data.model.CatalogSearchResponse
import com.example.pulseplay.data.model.EpisodeSearchResponse
import com.example.pulseplay.data.model.Playlist
import com.example.pulseplay.data.model.PlaylistSearchResponse
import com.example.pulseplay.data.model.PlaylistTracksPage
import com.example.pulseplay.data.model.ShowSearchResponse
import com.example.pulseplay.data.model.SpotifyTokenResponse
import com.example.pulseplay.data.model.TrackSearchResponse
import com.example.pulseplay.utils.ConfigUtils
import javax.inject.Inject

class PulsePlayRepository @Inject constructor(
    private val apiService: ApiService,
    private val authApiService: AuthApiService,
) {

    suspend fun exchangeCodeForToken(code: String, codeVerifier: String): SpotifyTokenResponse {
        return authApiService.exchangeCodeForToken(
            grantType = ConfigUtils.grantType,
            code = code,
            redirectUri = ConfigUtils.redirectUri,
            clientId = ConfigUtils.clientID,
            codeVerifier = codeVerifier
        )
    }

    /**
     * Spotify [GET /v1/search](https://developer.spotify.com/documentation/web-api/reference/search) —
     * multiple result types in one request.
     *
     * @param types Comma-separated Spotify types, e.g. `artist,track` or full catalog including
     * `episode,audiobook`.
     */
    suspend fun searchCatalog(
        query: String,
        token: String,
        types: String = "artist,track,album,playlist",
        market: String? = null ,
        limit: Int = 10,
        offset: Int = 0,
        includeExternal: String? = null,
    ): CatalogSearchResponse {
        return apiService.searchCatalog(
            query = query,
            types = types,
            market = market,
            limit = limit,
            offset = offset,
            includeExternal = includeExternal,
            token = token,
        )
    }

    suspend fun searchArtists(
        query: String,
        token: String,
        market: String? = null,
        limit: Int = 20,
        offset: Int = 0,
        includeExternal: String? = null,
    ): ArtistResponse {
        return apiService.searchArtists(
            query = query,
            market = market,
            limit = limit,
            offset = offset,
            includeExternal = includeExternal,
            token = token,
        )
    }

    suspend fun searchTracks(
        query: String,
        token: String,
        market: String? = null,
        limit: Int = 20,
        offset: Int = 0,
        includeExternal: String? = null,
    ): TrackSearchResponse {
        return apiService.searchTracks(
            query = query,
            market = market,
            limit = limit,
            offset = offset,
            includeExternal = includeExternal,
            token = token,
        )
    }

    suspend fun searchAlbums(
        query: String,
        token: String,
        market: String? = null,
        limit: Int = 20,
        offset: Int = 0,
        includeExternal: String? = null,
    ): AlbumSearchResponse {
        return apiService.searchAlbums(
            query = query,
            market = market,
            limit = limit,
            offset = offset,
            includeExternal = includeExternal,
            token = token,
        )
    }

    suspend fun searchPlaylists(
        query: String,
        token: String,
        market: String? = null,
        limit: Int = 20,
        offset: Int = 0,
        includeExternal: String? = null,
    ): PlaylistSearchResponse {
        return apiService.searchPlaylists(
            query = query,
            market = market,
            limit = limit,
            offset = offset,
            includeExternal = includeExternal,
            token = token,
        )
    }

    suspend fun searchShows(
        query: String,
        token: String,
        market: String? = null,
        limit: Int = 20,
        offset: Int = 0,
        includeExternal: String? = null,
    ): ShowSearchResponse {
        return apiService.searchShows(
            query = query,
            market = market,
            limit = limit,
            offset = offset,
            includeExternal = includeExternal,
            token = token,
        )
    }

    suspend fun searchEpisodes(
        query: String,
        token: String,
        market: String? = null,
        limit: Int = 20,
        offset: Int = 0,
        includeExternal: String? = null,
    ): EpisodeSearchResponse {
        return apiService.searchEpisodes(
            query = query,
            market = market,
            limit = limit,
            offset = offset,
            includeExternal = includeExternal,
            token = token,
        )
    }

    suspend fun searchAudiobooks(
        query: String,
        token: String,
        market: String? = null,
        limit: Int = 20,
        offset: Int = 0,
        includeExternal: String? = null,
    ): AudiobookSearchResponse {
        return apiService.searchAudiobooks(
            query = query,
            market = market,
            limit = limit,
            offset = offset,
            includeExternal = includeExternal,
            token = token,
        )
    }

    suspend fun getArtist(id: String, token: String): Artist =
        apiService.getArtist(id, token)

    suspend fun getArtistTopTracks(
        artistId: String,
        token: String,
        market: String = "IN",
    ): ArtistTopTracksResponse =
        apiService.getArtistTopTracks(artistId, market, token)

    suspend fun getAlbum(id: String, token: String): Album =
        apiService.getAlbum(id, token)

    suspend fun getAlbumTracks(
        albumId: String,
        token: String,
        market: String? = "IN",
    ): AlbumTracksPage =
        apiService.getAlbumTracks(albumId, market, 50, token)

    suspend fun getPlaylist(id: String, token: String): Playlist =
        apiService.getPlaylist(id, token)

    suspend fun getPlaylistTracks(
        playlistId: String,
        token: String,
        market: String? = "IN",
    ): PlaylistTracksPage =
        apiService.getPlaylistTracks(playlistId, market, 50, token)
}
