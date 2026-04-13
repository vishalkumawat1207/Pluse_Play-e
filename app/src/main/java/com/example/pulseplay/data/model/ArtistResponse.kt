package com.example.pulseplay.data.model

import com.google.gson.annotations.SerializedName

data class ArtistResponse(
    val artists: Artists,
)

/**
 * Paging object for search results (tracks, artists, albums, etc.).
 * See [GET /v1/search](https://developer.spotify.com/documentation/web-api/reference/search).
 */
data class Artists(
    val href: String? = null,
    val limit: Int? = null,
    val next: String? = null,
    val offset: Int? = null,
    val previous: String? = null,
    val total: Int? = null,
    val items: List<Artist> = emptyList(),
)

data class Artist(
    val name: String,
    val id: String,
    val images: List<Image>? = null,
)

data class Image(
    val url: String,
)

data class Track(
    val id: String,
    val name: String,
    val duration_ms: Long = 0L,
    val preview_url: String?,
    val artists: List<Artist> = emptyList(),
    /** Present on full track objects; optional on some API list endpoints. */
    val album: Album? = null,
)

data class TrackWrapper(
    val href: String? = null,
    val limit: Int? = null,
    val next: String? = null,
    val offset: Int? = null,
    val previous: String? = null,
    val total: Int? = null,
    val items: List<Track> = emptyList(),
)

data class TrackSearchResponse(val tracks: TrackWrapper)

data class Album(
    val id: String,
    val name: String,
    val release_date: String?,
    val total_tracks: Int?,
    val images: List<SpotifyImage>?,
    val artists: List<Artist>? = null,
)

data class AlbumWrapper(
    val href: String? = null,
    val limit: Int? = null,
    val next: String? = null,
    val offset: Int? = null,
    val previous: String? = null,
    val total: Int? = null,
    val items: List<Album> = emptyList(),
)

data class AlbumSearchResponse(val albums: AlbumWrapper)

data class PlaylistOwner(
    val id: String,
    val display_name: String?,
)

data class Playlist(
    val id: String,
    val name: String,
    val description: String?,
    val images: List<SpotifyImage>?,
    val owner: PlaylistOwner,
)

data class PlaylistWrapper(
    val href: String? = null,
    val limit: Int? = null,
    val next: String? = null,
    val offset: Int? = null,
    val previous: String? = null,
    val total: Int? = null,
    val items: List<Playlist> = emptyList(),
)

data class PlaylistSearchResponse(val playlists: PlaylistWrapper)

/**
 * Combined buckets from search when `type` lists multiple values. Omitted buckets are null.
 */
data class CatalogSearchResponse(
    val artists: Artists? = null,
    val tracks: TrackWrapper? = null,
    val albums: AlbumWrapper? = null,
    val playlists: PlaylistWrapper? = null,
    val shows: ShowWrapper? = null,
    val episodes: EpisodeWrapper? = null,
    @SerializedName("audiobooks")
    val audiobooks: AudioBookWrapper? = null,
)

data class Show(
    val id: String,
    val name: String,
    val publisher: String? = null,
    val description: String?,
    @SerializedName("images")
    val images: List<SpotifyImage>? = null,
    @SerializedName("total_episodes")
    val totalEpisodes: Int? = null,
)

data class ShowWrapper(
    val href: String? = null,
    val limit: Int? = null,
    val next: String? = null,
    val offset: Int? = null,
    val previous: String? = null,
    val total: Int? = null,
    val items: List<Show> = emptyList(),
)

data class ShowSearchResponse(val shows: ShowWrapper)

data class Episode(
    val id: String,
    val name: String,
    val description: String?,
    val duration_ms: Long,
    val release_date: String?,
    val images: List<SpotifyImage>?,
    /** Search may return simplified episodes without an embedded show. */
    val show: Show? = null,
)

data class EpisodeWrapper(
    val href: String? = null,
    val limit: Int? = null,
    val next: String? = null,
    val offset: Int? = null,
    val previous: String? = null,
    val total: Int? = null,
    val items: List<Episode> = emptyList(),
)

data class EpisodeSearchResponse(val episodes: EpisodeWrapper)

data class Author(val name: String)

data class AudioBook(
    val id: String,
    val name: String,
    val authors: List<Author>,
    val publisher: String? = null,
    val images: List<SpotifyImage>?,
    @SerializedName("total_chapters")
    val totalChapters: Int? = null,
)

data class AudioBookWrapper(
    val href: String? = null,
    val limit: Int? = null,
    val next: String? = null,
    val offset: Int? = null,
    val previous: String? = null,
    val total: Int? = null,
    val items: List<AudioBook> = emptyList(),
)

data class AudiobookSearchResponse(
    @SerializedName("audiobooks")
    val audiobooks: AudioBookWrapper,
)

data class SpotifyImage(
    val url: String,
    val height: Int?,
    val width: Int?,
)

data class ExternalUrls(
    val spotify: String?,
)

data class Followers(
    val total: Int,
)
