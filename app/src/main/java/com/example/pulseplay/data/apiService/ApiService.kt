package com.example.pulseplay.data.apiService

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
import com.example.pulseplay.data.model.TrackSearchResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Spotify Web API: [GET /v1/search](https://developer.spotify.com/documentation/web-api/reference/search)
 *
 * Base URL: `https://api.spotify.com/v1/` ([NetworkModule]).
 * Requires `Authorization: Bearer <access_token>`.
 *
 * Optional [market] is ISO 3166-1 alpha-2; with a user token, account country overrides.
 * [includeExternal] use `"audio"` when the client can play externally hosted audio.
 */
interface ApiService {

    @GET("artists/{id}")
    suspend fun getArtist(
        @Path("id") id: String,
        @Header("Authorization") token: String,
    ): Artist

    @GET("artists/{id}/top-tracks")
    suspend fun getArtistTopTracks(
        @Path("id") id: String,
        @Query("market") market: String = "IN",
        @Header("Authorization") token: String,
    ): ArtistTopTracksResponse

    @GET("albums/{id}")
    suspend fun getAlbum(
        @Path("id") id: String,
        @Header("Authorization") token: String,
    ): Album

    @GET("albums/{id}/tracks")
    suspend fun getAlbumTracks(
        @Path("id") id: String,
        @Query("market") market: String? = "IN",
        @Query("limit") limit: Int = 50,
        @Header("Authorization") token: String,
    ): AlbumTracksPage

    @GET("playlists/{id}")
    suspend fun getPlaylist(
        @Path("id") id: String,
        @Header("Authorization") token: String,
    ): Playlist

    @GET("playlists/{id}/tracks")
    suspend fun getPlaylistTracks(
        @Path("id") id: String,
        @Query("market") market: String? = "IN",
        @Query("limit") limit: Int = 50,
        @Header("Authorization") token: String,
    ): PlaylistTracksPage

    @GET("search")
    suspend fun searchArtists(
        @Query("q") query: String,
        @Query("type") type: String = "artist",
        @Query("market") market: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("include_external") includeExternal: String? = null,
        @Header("Authorization") token: String,
    ): ArtistResponse

    /**
     * Multi-bucket search: set [types] to a comma-separated list, e.g.
     * `artist,track,album,playlist,show,episode,audiobook`.
     */
    @GET("search")
    suspend fun searchCatalog(
        @Query("q") query: String,
        @Query("type") types: String = "artist,track,album,playlist,show,episode,audiobook",
        @Query("market") market: String? = null,
        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int = 0,
        @Query("include_external") includeExternal: String? = null,
        @Header("Authorization") token: String,
    ): CatalogSearchResponse

    @GET("search")
    suspend fun searchTracks(
        @Query("q") query: String,
        @Query("type") type: String = "track",
        @Query("market") market: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("include_external") includeExternal: String? = null,
        @Header("Authorization") token: String,
    ): TrackSearchResponse

    @GET("search")
    suspend fun searchAlbums(
        @Query("q") query: String,
        @Query("type") type: String = "album",
        @Query("market") market: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("include_external") includeExternal: String? = null,
        @Header("Authorization") token: String,
    ): AlbumSearchResponse

    @GET("search")
    suspend fun searchPlaylists(
        @Query("q") query: String,
        @Query("type") type: String = "playlist",
        @Query("market") market: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("include_external") includeExternal: String? = null,
        @Header("Authorization") token: String,
    ): PlaylistSearchResponse

    @GET("search")
    suspend fun searchShows(
        @Query("q") query: String,
        @Query("type") type: String = "show",
        @Query("market") market: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("include_external") includeExternal: String? = null,
        @Header("Authorization") token: String,
    ): ShowSearchResponse

    @GET("search")
    suspend fun searchEpisodes(
        @Query("q") query: String,
        @Query("type") type: String = "episode",
        @Query("market") market: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("include_external") includeExternal: String? = null,
        @Header("Authorization") token: String,
    ): EpisodeSearchResponse

    @GET("search")
    suspend fun searchAudiobooks(
        @Query("q") query: String,
        @Query("type") type: String = "audiobook",
        @Query("market") market: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("include_external") includeExternal: String? = null,
        @Header("Authorization") token: String,
    ): AudiobookSearchResponse
}
