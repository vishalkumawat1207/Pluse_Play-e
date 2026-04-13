package com.example.pulseplay.ui.screen

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.pulseplay.ui.viewModel.home.PreviewPlayerViewModel
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

private val AccentGreen = Color(0xFF1DB954)
private val OnDark = Color.White
private val Muted = Color(0xFFB3B3B3)

private fun formatPlaybackTime(ms: Long): String {
    val totalSec = (ms / 1000).toInt().coerceAtLeast(0)
    val m = totalSec / 60
    val s = totalSec % 60
    return "%d:%02d".format(m, s)
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen(
    navController: NavController,
    previewPlayerViewModel: PreviewPlayerViewModel = hiltViewModel(LocalContext.current as ComponentActivity),
) {
    val state by previewPlayerViewModel.uiState.collectAsStateWithLifecycle()
    val queue = previewPlayerViewModel.queueSnapshot()
    val context = LocalContext.current

    LaunchedEffect(state.hasActiveSession) {
        if (!state.hasActiveSession || queue.isEmpty()) {
            navController.popBackStack()
        }
    }

    if (queue.isEmpty() || !state.hasActiveSession) {
        return
    }

    val scope = rememberCoroutineScope()
    val safeIndex = state.currentIndex.coerceIn(0, queue.lastIndex)
    val pagerState = rememberPagerState(
        initialPage = safeIndex,
        pageCount = { queue.size },
    )

    LaunchedEffect(safeIndex, queue.size) {
        if (pagerState.currentPage != safeIndex && safeIndex in queue.indices) {
            scope.launch {
                pagerState.animateScrollToPage(safeIndex)
            }
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.isScrollInProgress to pagerState.currentPage }
            .collect { (scrolling, page) ->
                if (!scrolling && page != state.currentIndex && page in queue.indices) {
                    previewPlayerViewModel.seekToQueueIndex(page)
                }
            }
    }

    var sliderDragging by remember { mutableStateOf(false) }
    var sliderValue by remember { mutableFloatStateOf(0f) }
    val progress = if (state.durationMs > 0) {
        (state.positionMs.toFloat() / state.durationMs.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    val hint = state.progressHint
                    Text(
                        buildAnnotatedString {
                            append("Playlist ")
                            withStyle(SpanStyle(color = AccentGreen, fontWeight = FontWeight.Bold)) {
                                append(hint)
                            }
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = OnDark,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* settings placeholder */ }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Muted,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = OnDark,
                    navigationIconContentColor = OnDark,
                ),
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF3D1518),
                            Color(0xFF12080C),
                            Color(0xFF050505),
                        ),
                    ),
                )
                .padding(padding),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                HorizontalPager(
                    state = pagerState,
                    contentPadding = PaddingValues(horizontal = 48.dp),
                    pageSpacing = 12.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                ) { page ->
                    val item = queue[page]
                    val offset = (pagerState.currentPage - page + pagerState.currentPageOffsetFraction).absoluteValue
                    val pageArtScale = 0.88f + (1f - offset.coerceIn(0f, 1f)) * 0.12f
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center,
                    ) {
                        val art = item.artworkUrl
                        if (art != null) {
                            AsyncImage(
                                model = art,
                                contentDescription = item.title,
                                modifier = Modifier
                                    .fillMaxWidth(0.92f)
                                    .aspectRatio(1f)
                                    .scale(pageArtScale)
                                    .clip(RoundedCornerShape(24.dp))
                                    .align(Alignment.Center),
                                contentScale = ContentScale.Crop,
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.92f)
                                    .aspectRatio(1f)
                                    .scale(pageArtScale)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(Color(0xFF282828)),
                            )
                        }
                    }
                }

                Text(
                    state.currentTitle,
                    color = OnDark,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 24.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    state.currentSubtitle,
                    color = Muted,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 24.dp),
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xE6121212),
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                "⌄",
                                color = Muted,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(bottom = 4.dp),
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                formatPlaybackTime(state.positionMs),
                                color = Muted,
                                fontSize = 12.sp,
                            )
                            Text(
                                formatPlaybackTime(state.durationMs),
                                color = Muted,
                                fontSize = 12.sp,
                            )
                        }
                        Slider(
                            value = if (sliderDragging) sliderValue else progress,
                            onValueChange = {
                                sliderDragging = true
                                sliderValue = it
                            },
                            onValueChangeFinished = {
                                sliderDragging = false
                                previewPlayerViewModel.seekTo((sliderValue * state.durationMs).toLong())
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = SliderDefaults.colors(
                                thumbColor = OnDark,
                                activeTrackColor = AccentGreen,
                                inactiveTrackColor = Color(0xFF444444),
                            ),
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            val repeatTint =
                                if (state.repeatMode != Player.REPEAT_MODE_OFF) AccentGreen else Muted
                            IconButton(onClick = { previewPlayerViewModel.cycleRepeatMode() }) {
                                Icon(
                                    imageVector = when (state.repeatMode) {
                                        Player.REPEAT_MODE_ONE -> Icons.Default.RepeatOne
                                        else -> Icons.Default.Repeat
                                    },
                                    contentDescription = "Repeat",
                                    tint = repeatTint,
                                )
                            }
                            IconButton(onClick = { previewPlayerViewModel.skipPrevious() }) {
                                Icon(
                                    Icons.Default.SkipPrevious,
                                    contentDescription = "Previous",
                                    tint = OnDark,
                                    modifier = Modifier.size(36.dp),
                                )
                            }
                            IconButton(
                                onClick = { previewPlayerViewModel.togglePlayPause() },
                                modifier = Modifier.size(72.dp),
                            ) {
                                Icon(
                                    if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (state.isPlaying) "Pause" else "Play",
                                    tint = AccentGreen,
                                    modifier = Modifier.size(56.dp),
                                )
                            }
                            IconButton(onClick = { previewPlayerViewModel.skipNext() }) {
                                Icon(
                                    Icons.Default.SkipNext,
                                    contentDescription = "Next",
                                    tint = OnDark,
                                    modifier = Modifier.size(36.dp),
                                )
                            }
                            val shuffleTint = if (state.shuffleEnabled) AccentGreen else Muted
                            IconButton(onClick = { previewPlayerViewModel.toggleShuffle() }) {
                                Icon(
                                    Icons.Default.Shuffle,
                                    contentDescription = "Shuffle",
                                    tint = shuffleTint,
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(
                                modifier = Modifier
                                    .clickable {
                                        val send = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(
                                                Intent.EXTRA_TEXT,
                                                "${state.currentTitle} — ${state.currentSubtitle}",
                                            )
                                        }
                                        context.startActivity(Intent.createChooser(send, "Share track"))
                                    }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = Muted,
                                    modifier = Modifier.size(20.dp),
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                                Text("Share", color = Muted, fontSize = 14.sp)
                            }
                            Row(
                                modifier = Modifier
                                    .clickable { /* add to playlist — future */ }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text("Add playlist", color = Muted, fontSize = 14.sp)
                                Spacer(modifier = Modifier.size(8.dp))
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Add to playlist",
                                    tint = Muted,
                                    modifier = Modifier.size(20.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
