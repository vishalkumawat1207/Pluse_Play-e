package com.example.pulseplay.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.pulseplay.R
import com.example.pulseplay.data.model.Album
import com.example.pulseplay.data.model.Artist
import com.example.pulseplay.data.model.AudioBook
import com.example.pulseplay.data.model.Episode
import com.example.pulseplay.data.model.Playlist
import com.example.pulseplay.data.model.Show
import com.example.pulseplay.data.model.Track
import com.example.pulseplay.navigation.Screen
import com.example.pulseplay.ui.components.PulseMiniPlayer
import com.example.pulseplay.ui.theme.PulsePlayTheme
import com.example.pulseplay.ui.viewModel.home.HomeViewModel
import com.example.pulseplay.ui.viewModel.home.PreviewPlayerViewModel
import kotlinx.coroutines.flow.collectLatest

private val SpotifyBackground = Color(0xFF121212)
private val SpotifyNavBar = Color(0xFF0F1929)
private val SpotifyOnDark = Color.White
private val SpotifyMuted = Color(0xFFB3B3B3)
private val SpotifyFieldInk = Color(0xFF121212)

private data class BrowseTile(
    val title: String,
    val color: Color,
    @DrawableRes val accentRes: Int? = null,
)

private val browseTiles = listOf(
    BrowseTile("Pop", Color(0xFF75A768), R.drawable.ic_accent_music_note),
    BrowseTile("Podcasts", Color(0xFF8768A7), R.drawable.ic_accent_podcast),
    BrowseTile("Charts", Color(0xFF9854B2), R.drawable.ic_accent_music_note),
    BrowseTile("Indie", Color(0xFF678026), R.drawable.ic_accent_music_note),
    BrowseTile("Rock", Color(0xFF3371E4), R.drawable.ic_accent_music_note),
    BrowseTile("Party", Color(0xFFCF4321), R.drawable.ic_accent_music_note),
    BrowseTile("Focus", Color(0xFFABBB6D), R.drawable.ic_accent_music_note),
    BrowseTile("Sleep", Color(0xFF223160), R.drawable.ic_accent_music_note),
)

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = SpotifyOnDark,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
    )
}

@Composable
private fun BrowseTileCard(tile: BrowseTile, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1.85f)
            .clip(RoundedCornerShape(4.dp))
            .clickable(onClick = onClick)
            .background(tile.color)
            .padding(12.dp),
    ) {
        Text(
            text = tile.title,
            color = SpotifyOnDark,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(0.65f),
        )
        tile.accentRes?.let { resId ->
            Icon(
                painter = painterResource(resId),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(40.dp),
                tint = Color.Unspecified,
            )
        }
    }
}

@Composable
private fun ArtistSearchRow(artist: Artist, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
    ) {
        val url = artist.images?.firstOrNull()?.url
        if (url != null) {
            AsyncImage(
                model = url,
                contentDescription = artist.name,
                modifier = Modifier.size(56.dp),
            )
        } else {
            Spacer(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFF282828), RoundedCornerShape(4.dp)),
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            artist.name,
            color = SpotifyOnDark,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun TrackSearchRow(track: Track, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
    ) {
        val url = track.album?.images?.firstOrNull()?.url
        if (url != null) {
            AsyncImage(
                model = url,
                contentDescription = track.name,
                modifier = Modifier.size(56.dp),
            )
        } else {
            Spacer(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFF282828), RoundedCornerShape(4.dp)),
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                track.name,
                color = SpotifyOnDark,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            val subtitle = track.artists.joinToString { it.name }
            if (subtitle.isNotEmpty()) {
                Text(
                    subtitle,
                    color = SpotifyMuted,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (track.preview_url != null) {
                Text(
                    "30s preview",
                    color = Color(0xFF1DB954),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
    }
}

@Composable
private fun AlbumSearchRow(album: Album, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
    ) {
        val url = album.images?.firstOrNull()?.url
        if (url != null) {
            AsyncImage(
                model = url,
                contentDescription = album.name,
                modifier = Modifier.size(56.dp),
            )
        } else {
            Spacer(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFF282828), RoundedCornerShape(4.dp)),
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                album.name,
                color = SpotifyOnDark,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            val subtitle = album.artists?.joinToString { it.name }.orEmpty()
            if (subtitle.isNotEmpty()) {
                Text(
                    subtitle,
                    color = SpotifyMuted,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun PlaylistSearchRow(playlist: Playlist, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
    ) {
        val url = playlist.images?.firstOrNull()?.url
        if (url != null) {
            AsyncImage(
                model = url,
                contentDescription = playlist.name,
                modifier = Modifier.size(56.dp),
            )
        } else {
            Spacer(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFF282828), RoundedCornerShape(4.dp)),
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                playlist.name,
                color = SpotifyOnDark,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            playlist.owner.display_name?.takeIf { it.isNotBlank() }?.let { owner ->
                Text(
                    owner,
                    color = SpotifyMuted,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun ShowSearchRow(show: Show) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 6.dp),
    ) {
        val url = show.images?.firstOrNull()?.url
        if (url != null) {
            AsyncImage(
                model = url,
                contentDescription = show.name,
                modifier = Modifier.size(56.dp),
            )
        } else {
            Spacer(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFF282828), RoundedCornerShape(4.dp)),
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                show.name,
                color = SpotifyOnDark,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            show.publisher?.takeIf { it.isNotBlank() }?.let { publisher ->
                Text(
                    publisher,
                    color = SpotifyMuted,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun EpisodeSearchRow(episode: Episode) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 6.dp),
    ) {
        val url = episode.images?.firstOrNull()?.url
        if (url != null) {
            AsyncImage(
                model = url,
                contentDescription = episode.name,
                modifier = Modifier.size(56.dp),
            )
        } else {
            Spacer(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFF282828), RoundedCornerShape(4.dp)),
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                episode.name,
                color = SpotifyOnDark,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            episode.show?.name?.takeIf { it.isNotBlank() }?.let { showName ->
                Text(
                    showName,
                    color = SpotifyMuted,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun AudiobookSearchRow(book: AudioBook) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 6.dp),
    ) {
        val url = book.images?.firstOrNull()?.url
        if (url != null) {
            AsyncImage(
                model = url,
                contentDescription = book.name,
                modifier = Modifier.size(56.dp),
            )
        } else {
            Spacer(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFF282828), RoundedCornerShape(4.dp)),
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                book.name,
                color = SpotifyOnDark,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            val authors = book.authors.joinToString { it.name }
            if (authors.isNotBlank()) {
                Text(
                    authors,
                    color = SpotifyMuted,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController,
) {
    val context = LocalContext.current
    val activity = context as ComponentActivity
    val previewPlayerViewModel = hiltViewModel<PreviewPlayerViewModel>(activity)
    val playerState by previewPlayerViewModel.uiState.collectAsStateWithLifecycle()
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when (
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS,
                )
            ) {
                PackageManager.PERMISSION_GRANTED -> {}
                else -> notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    val artists = viewModel.artists
    val tracks = viewModel.tracks
    val albums = viewModel.albums
    val playlists = viewModel.playlists
    val shows = viewModel.shows
    val episodes = viewModel.episodes
    val audiobooks = viewModel.audiobooks
    val isLoading = viewModel.isLoading

    var searchQuery by remember { mutableStateOf("") }
    var activeQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(1) }
    val focusManager = LocalFocusManager.current
    val tag = "HomeScreen"

    val hasAnyResults = artists.isNotEmpty() ||
        tracks.isNotEmpty() ||
        albums.isNotEmpty() ||
        playlists.isNotEmpty() ||
        shows.isNotEmpty() ||
        episodes.isNotEmpty() ||
        audiobooks.isNotEmpty()

    LaunchedEffect(Unit) {
        viewModel.navigateToLogOutPopUp.collectLatest {
            Log.d(tag, "coming to navigateToLogOutPopUp")
            navController.navigate(Screen.Logout.route) {
                popUpTo(Screen.Home.route) { inclusive = true }
            }
        }
    }

    val showBrowse = activeQuery.isBlank()

    Scaffold(
        containerColor = SpotifyBackground,
        bottomBar = {
            Column {
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
                NavigationBar(
                    containerColor = SpotifyNavBar,
                    contentColor = SpotifyOnDark,
                ) {
                val itemColors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SpotifyOnDark,
                    selectedTextColor = SpotifyOnDark,
                    indicatorColor = Color(0xFF1A1A1A),
                    unselectedIconColor = SpotifyMuted,
                    unselectedTextColor = SpotifyMuted,
                )
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            painterResource(R.drawable.ic_nav_home),
                            contentDescription = "Home",
                        )
                    },
                    label = { Text("Home") },
                    colors = itemColors,
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            painterResource(R.drawable.ic_nav_search),
                            contentDescription = "Search",
                        )
                    },
                    label = { Text("Search") },
                    colors = itemColors,
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Icon(
                            painterResource(R.drawable.ic_nav_library),
                            contentDescription = "Your Library",
                        )
                    },
                    label = { Text("Library") },
                    colors = itemColors,
                )
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(SpotifyBackground)
                .padding(horizontal = 16.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Search",
                    color = SpotifyOnDark,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                )
                Icon(
                    painter = painterResource(R.drawable.ic_nav_profile),
                    contentDescription = "Profile",
                    tint = SpotifyOnDark,
                    modifier = Modifier.size(32.dp),
                )
            }

            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = {
                    Text(
                        "What do you want to listen to?",
                        color = Color(0xFF6A6A6A),
                    )
                },
                leadingIcon = {
                    Icon(
                        painterResource(R.drawable.ic_nav_search),
                        contentDescription = null,
                        tint = SpotifyFieldInk,
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty() || activeQuery.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                searchQuery = ""
                                activeQuery = ""
                                viewModel.clearSearchResults()
                                focusManager.clearFocus()
                            },
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = SpotifyFieldInk,
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(4.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedTextColor = SpotifyFieldInk,
                    unfocusedTextColor = SpotifyFieldInk,
                    cursorColor = SpotifyFieldInk,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        val q = searchQuery.trim()
                        if (q.isNotEmpty()) {
                            activeQuery = q
                            viewModel.search(q)
                        }
                        focusManager.clearFocus()
                    },
                ),
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                showBrowse -> {
                    Text(
                        text = "Browse all",
                        color = SpotifyOnDark,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 12.dp),
                    )
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(bottom = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(browseTiles) { tile ->
                            BrowseTileCard(tile = tile) {
                                searchQuery = tile.title
                                activeQuery = tile.title
                                viewModel.search(tile.title)
                            }
                        }
                    }
                }

                isLoading -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = SpotifyOnDark)
                    }
                }

                !hasAnyResults -> {
                    Text(
                        text = "No results for \"$activeQuery\". Try another search.",
                        color = SpotifyMuted,
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 24.dp),
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(bottom = 24.dp),
                    ) {
                        if (artists.isNotEmpty()) {
                            item { SectionTitle("Artists") }
                            items(artists) { artist ->
                                ArtistSearchRow(artist) {
                                    navController.navigate(Screen.ArtistDetail.createRoute(artist.id))
                                }
                            }
                        }
                        if (tracks.isNotEmpty()) {
                            item { SectionTitle("Songs") }
                            items(tracks) { track ->
                                TrackSearchRow(track = track) {
                                    previewPlayerViewModel.playTrack(track, tracks)
                                }
                            }
                        }
                        if (albums.isNotEmpty()) {
                            item { SectionTitle("Albums") }
                            items(albums) { album ->
                                AlbumSearchRow(album) {
                                    navController.navigate(Screen.AlbumDetail.createRoute(album.id))
                                }
                            }
                        }
                        if (playlists.isNotEmpty()) {
                            item { SectionTitle("Playlists") }
                            items(playlists) { playlist ->
                                PlaylistSearchRow(playlist) {
                                    navController.navigate(Screen.PlaylistDetail.createRoute(playlist.id))
                                }
                            }
                        }
                        if (shows.isNotEmpty()) {
                            item { SectionTitle("Podcasts & shows") }
                            items(shows) { ShowSearchRow(it) }
                        }
                        if (episodes.isNotEmpty()) {
                            item { SectionTitle("Episodes") }
                            items(episodes) { EpisodeSearchRow(it) }
                        }
                        if (audiobooks.isNotEmpty()) {
                            item { SectionTitle("Audiobooks") }
                            items(audiobooks) { AudiobookSearchRow(it) }
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreenModel() {
}

@Preview(showBackground = true, apiLevel = 33)
@Composable
fun GreetingPreview() {
    val navController = rememberNavController()
    PulsePlayTheme {
        HomeScreen(navController = navController)
    }
}
