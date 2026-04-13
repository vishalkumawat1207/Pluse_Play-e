package com.example.pulseplay.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.pulseplay.ui.screen.AlbumDetailScreen
import com.example.pulseplay.ui.screen.ArtistDetailScreen
import com.example.pulseplay.ui.screen.AuthScreen
import com.example.pulseplay.ui.screen.HomeScreen
import com.example.pulseplay.ui.screen.NowPlayingScreen
import com.example.pulseplay.ui.screen.PlaylistDetailScreen
import com.example.pulseplay.ui.screen.dialog.ShowLogoutPopup
import com.example.pulseplay.ui.screen.handler.RedirectHandler

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route,
    modifier: Modifier = Modifier,
) {

    val TAG = "AppNavGraph"

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {

        composable(Screen.Login.route) {
            AuthScreen(navController = navController)
        }

        composable(Screen.Redirect.route) {
            RedirectHandler(navController = navController)
        }

        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.NowPlaying.route) {
            NowPlayingScreen(navController = navController)
        }
        composable(
            route = Screen.PlaylistDetail.route,
            arguments = listOf(
                navArgument("playlistId") { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getString("playlistId")
            PlaylistDetailScreen(
                navController = navController,
                playlistId = playlistId,
            )
        }
        composable(
            route = Screen.ArtistDetail.route,
            arguments = listOf(
                navArgument("artistId") { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val artistId = backStackEntry.arguments?.getString("artistId")
            ArtistDetailScreen(
                navController = navController,
                artistId = artistId,
            )
        }
        composable(
            route = Screen.AlbumDetail.route,
            arguments = listOf(
                navArgument("albumId") { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val albumId = backStackEntry.arguments?.getString("albumId")
            AlbumDetailScreen(
                navController = navController,
                albumId = albumId,
            )
        }
        composable(Screen.Logout.route) {
            Log.d(TAG, "coming to navigateToLogOutPopUp")
            ShowLogoutPopup(navController = navController)
        }
    }
}
