package com.example.pulseplay.data.model

/**
 * [GET /v1/artists/{id}/top-tracks](https://developer.spotify.com/documentation/web-api/reference/get-an-artists-top-tracks)
 */
data class ArtistTopTracksResponse(
    val tracks: List<Track> = emptyList(),
)

/**
 * [GET /v1/albums/{id}/tracks](https://developer.spotify.com/documentation/web-api/reference/get-an-albums-tracks)
 */
data class AlbumTracksPage(
    val items: List<Track> = emptyList(),
)

/**
 * [GET /v1/playlists/{id}/tracks](https://developer.spotify.com/documentation/web-api/reference/get-playlists-tracks)
 */
data class PlaylistTrackItem(
    val track: Track? = null,
)

data class PlaylistTracksPage(
    val items: List<PlaylistTrackItem> = emptyList(),
)
