package com.example.pulseplay.ui.screen

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.pulseplay.data.model.Track
import com.example.pulseplay.navigation.Screen
import com.example.pulseplay.ui.components.PulseMiniPlayer
import com.example.pulseplay.ui.viewModel.detail.AlbumDetailViewModel
import com.example.pulseplay.ui.viewModel.detail.ArtistDetailViewModel
import com.example.pulseplay.ui.viewModel.detail.PlaylistDetailViewModel
import com.example.pulseplay.ui.viewModel.home.PreviewPlayerViewModel

private val DetailBg = Color(0xFF121212)
private val DetailOnDark = Color.White
private val DetailMuted = Color(0xFFB3B3B3)

@Composable
private fun DetailTrackRow(track: Track, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val url = track.album?.images?.firstOrNull()?.url
        if (url != null) {
            AsyncImage(
                model = url,
                contentDescription = track.name,
                modifier = Modifier.size(48.dp),
            )
        } else {
            Spacer(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF282828), RoundedCornerShape(4.dp)),
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                track.name,
                color = DetailOnDark,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            val subtitle = track.artists.joinToString { it.name }
            if (subtitle.isNotEmpty()) {
                Text(
                    subtitle,
                    color = DetailMuted,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                "Demo audio (10 clips · shuffled)",
                color = Color(0xFF1DB954),
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicDetailScaffold(
    navController: NavController,
    previewPlayerViewModel: PreviewPlayerViewModel,
    headline: String,
    subline: String?,
    imageUrl: String?,
    tracks: List<Track>,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit,
    onTrackClick: (Track, Int) -> Unit,
) {
    val playerState by previewPlayerViewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        containerColor = DetailBg,
        bottomBar = {
            if (playerState.hasActiveSession) {
                PulseMiniPlayer(
                    title = playerState.currentTitle,
                    subtitle = playerState.currentSubtitle,
                    artworkUrl = playerState.artworkUrl,
                    isPlaying = playerState.isPlaying,
                    positionMs = playerState.positionMs,
                    durationMs = playerState.durationMs,
                    progressHint = playerState.progressHint,
                    onToggle = previewPlayerViewModel::togglePlayPause,
                    onNext = previewPlayerViewModel::skipNext,
                    onPrevious = previewPlayerViewModel::skipPrevious,
                    onOpenNowPlaying = {
                        navController.navigate(Screen.NowPlaying.route) {
                            launchSingleTop = true
                        }
                    },
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        headline,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DetailBg,
                    titleContentColor = DetailOnDark,
                    navigationIconContentColor = DetailOnDark,
                ),
            )
        },
    ) { padding ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = DetailOnDark)
                }
            }
            error != null -> {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(error, color = DetailMuted)
                    TextButton(onClick = onRetry) {
                        Text("Retry")
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                ) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val url = imageUrl
                            if (url != null) {
                                AsyncImage(
                                    model = url,
                                    contentDescription = headline,
                                    modifier = Modifier.size(120.dp),
                                )
                            } else {
                                Spacer(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .background(Color(0xFF282828)),
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    headline,
                                    color = DetailOnDark,
                                    style = MaterialTheme.typography.titleLarge,
                                )
                                subline?.takeIf { it.isNotBlank() }?.let {
                                    Text(
                                        it,
                                        color = DetailMuted,
                                        fontSize = 14.sp,
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            "Songs",
                            color = DetailOnDark,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    if (tracks.isEmpty()) {
                        item {
                            Text("No songs in this list.", color = DetailMuted)
                        }
                    } else {
                        itemsIndexed(tracks) { index, track ->
                            DetailTrackRow(track = track, onClick = { onTrackClick(track, index) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArtistDetailScreen(navController: NavController, artistId: String?) {
    val vm: ArtistDetailViewModel = hiltViewModel()
    val activity = LocalContext.current as ComponentActivity
    val previewVm = hiltViewModel<PreviewPlayerViewModel>(activity)
    LaunchedEffect(artistId) {
        artistId?.let { vm.load(it) }
    }
    MusicDetailScaffold(
        navController = navController,
        previewPlayerViewModel = previewVm,
        headline = vm.title.ifBlank { "Artist" },
        subline = null,
        imageUrl = vm.imageUrl,
        tracks = vm.tracks,
        isLoading = vm.isLoading,
        error = vm.error,
        onRetry = { artistId?.let { vm.load(it) } },
        onTrackClick = { _, index ->
            previewVm.playTracksWithLocalSample(vm.tracks, index, vm.imageUrl)
        },
    )
}

@Composable
fun AlbumDetailScreen(navController: NavController, albumId: String?) {
    val vm: AlbumDetailViewModel = hiltViewModel()
    val activity = LocalContext.current as ComponentActivity
    val previewVm = hiltViewModel<PreviewPlayerViewModel>(activity)
    LaunchedEffect(albumId) {
        albumId?.let { vm.load(it) }
    }
    MusicDetailScaffold(
        navController = navController,
        previewPlayerViewModel = previewVm,
        headline = vm.title.ifBlank { "Album" },
        subline = vm.subtitle,
        imageUrl = vm.imageUrl,
        tracks = vm.tracks,
        isLoading = vm.isLoading,
        error = vm.error,
        onRetry = { albumId?.let { vm.load(it) } },
        onTrackClick = { _, index ->
            previewVm.playTracksWithLocalSample(vm.tracks, index, vm.imageUrl)
        },
    )
}

@Composable
fun PlaylistDetailScreen(navController: NavController, playlistId: String?) {
    val vm: PlaylistDetailViewModel = hiltViewModel()
    val activity = LocalContext.current as ComponentActivity
    val previewVm = hiltViewModel<PreviewPlayerViewModel>(activity)
    LaunchedEffect(playlistId) {
        playlistId?.let { vm.load(it) }
    }
    MusicDetailScaffold(
        navController = navController,
        previewPlayerViewModel = previewVm,
        headline = vm.title.ifBlank { "Playlist" },
        subline = vm.subtitle,
        imageUrl = vm.imageUrl,
        tracks = vm.tracks,
        isLoading = vm.isLoading,
        error = vm.error,
        onRetry = { playlistId?.let { vm.load(it) } },
        onTrackClick = { _, index ->
            previewVm.playTracksWithLocalSample(vm.tracks, index, vm.imageUrl)
        },
    )
}
