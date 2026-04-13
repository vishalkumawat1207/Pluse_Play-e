package com.example.pulseplay.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login_screen")
    data object Redirect : Screen("redirect")
    data object Home : Screen("home_screen")
    data object NowPlaying : Screen("now_playing")
    data object PlaylistDetail : Screen("playlist_detail_screen/{playlistId}") {
        fun createRoute(playlistId: String) = "playlist_detail_screen/$playlistId"
    }

    data object ArtistDetail : Screen("artist_detail/{artistId}") {
        fun createRoute(artistId: String) = "artist_detail/$artistId"
    }

    data object AlbumDetail : Screen("album_detail/{albumId}") {
        fun createRoute(albumId: String) = "album_detail/$albumId"
    }

    data object About : Screen("about_screen")
    data object Logout : Screen("logout_screen")

}