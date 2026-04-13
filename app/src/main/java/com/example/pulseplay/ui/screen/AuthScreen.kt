package com.example.pulseplay.ui.screen

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pulseplay.R
import com.example.pulseplay.ui.viewModel.auth.AuthViewModel

private val AuthBackground = Color(0xFF121212)
private val SpotifyGreen = Color(0xFF1DB954)
private val OnDark = Color.White

@Composable
@Suppress("UNUSED_PARAMETER")
fun AuthScreen(viewModel: AuthViewModel = hiltViewModel(), navController: NavController) {
    val context = LocalContext.current
    val authUri by viewModel.authUrl.observeAsState()
    val tag = "AuthScreen"

    LaunchedEffect(authUri) {
        authUri?.let {
            Log.d(tag, "uri : $authUri")
            val intent = Intent(Intent.ACTION_VIEW, authUri)
            context.startActivity(intent)
        }
    }

    val startSpotifyAuth: () -> Unit = { viewModel.startAuthFlow(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AuthBackground),
    ) {
        WelcomeAlbumCollage(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.pulse_play_launcher_icon),
                contentDescription = null,
                modifier = Modifier.size(72.dp),
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.auth_headline),
                color = OnDark,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 32.sp,
            )
            Spacer(modifier = Modifier.height(28.dp))
            Button(
                onClick = startSpotifyAuth,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SpotifyGreen,
                    contentColor = Color.Black,
                ),
            ) {
                Text(
                    text = stringResource(R.string.auth_sign_up_free),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.auth_log_in),
                color = OnDark,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clickable(onClick = startSpotifyAuth)
                    .padding(8.dp),
            )
        }
    }
}

@Composable
private fun WelcomeAlbumCollage(modifier: Modifier = Modifier) {
    BoxWithConstraints(
        modifier = modifier
            .background(Color(0xFF0A0A0A)),
    ) {
        val w = maxWidth
        val h = maxHeight
        Box(modifier = Modifier.fillMaxSize()) {
            CollageTile(
                modifier = Modifier
                    .size(w * 0.42f, h * 0.28f)
                    .offset(x = w * 0.04f, y = h * 0.12f)
                    .rotate(-14f),
                brush = Brush.linearGradient(
                    listOf(Color(0xFF1DB954), Color(0xFF0D5C2E)),
                ),
                shape = RoundedCornerShape(10.dp),
            )
            CollageTile(
                modifier = Modifier
                    .size(w * 0.22f)
                    .offset(x = w * 0.52f, y = h * 0.06f)
                    .rotate(18f),
                brush = Brush.linearGradient(
                    listOf(Color(0xFF7B5FD7), Color(0xFF4A2F9E)),
                ),
                shape = RoundedCornerShape(10.dp),
            )
            CollageTile(
                modifier = Modifier
                    .size(w * 0.26f)
                    .offset(x = w * 0.68f, y = h * 0.38f)
                    .rotate(-10f),
                brush = Brush.linearGradient(
                    listOf(Color(0xFFE53935), Color(0xFF9A1F1F)),
                ),
                shape = RoundedCornerShape(10.dp),
            )
            CollageTile(
                modifier = Modifier
                    .size(w * 0.2f)
                    .offset(x = w * 0.08f, y = h * 0.48f)
                    .rotate(22f),
                brush = Brush.linearGradient(
                    listOf(Color(0xFF3D5AFE), Color(0xFF1A237E)),
                ),
                shape = CircleShape,
            )
            CollageTile(
                modifier = Modifier
                    .size(w * 0.34f, h * 0.22f)
                    .offset(x = w * 0.32f, y = h * 0.55f)
                    .rotate(6f),
                brush = Brush.linearGradient(
                    listOf(Color(0xFFFF7043), Color(0xFFBF360C)),
                ),
                shape = RoundedCornerShape(10.dp),
            )
            CollageTile(
                modifier = Modifier
                    .size(w * 0.18f)
                    .offset(x = w * 0.78f, y = h * 0.68f)
                    .rotate(-20f),
                brush = Brush.linearGradient(
                    listOf(Color(0xFFE91E8C), Color(0xFF880E4F)),
                ),
                shape = RoundedCornerShape(10.dp),
            )
            CollageTile(
                modifier = Modifier
                    .size(w * 0.24f)
                    .offset(x = w * 0.02f, y = h * 0.72f)
                    .rotate(12f),
                brush = Brush.linearGradient(
                    listOf(Color(0xFF00BCD4), Color(0xFF006064)),
                ),
                shape = RoundedCornerShape(10.dp),
            )
        }
    }
}

@Composable
private fun CollageTile(
    modifier: Modifier,
    brush: Brush,
    shape: Shape,
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(brush),
    )
}
