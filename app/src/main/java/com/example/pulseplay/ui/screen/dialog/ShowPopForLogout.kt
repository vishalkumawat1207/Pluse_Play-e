package com.example.pulseplay.ui.screen.dialog

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pulseplay.R
import com.example.pulseplay.navigation.Screen
import com.example.pulseplay.ui.theme.PulsePlayTheme
import com.example.pulseplay.ui.viewModel.home.AppLogoutViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ShowLogoutPopup(viewModel: AppLogoutViewModel = hiltViewModel(), navController: NavController) {
    val TAG = "ShowLogoutPopup"

    LaunchedEffect(Unit) {
        viewModel.navigateToLogin.collectLatest {
            Log.d(TAG, "coming to navigateToLogin ")
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Home.route) { inclusive = true }
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF7E7E7E)) // Background similar to XML
    ) {
        // Close Button

        Image(
            painter = painterResource(id = R.drawable.ic_close),
            contentDescription = "Close Dialog",
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.TopEnd)
                .padding(top = 10.dp, end = 16.dp)
                .clickable {
                    viewModel.goToLogInPage()
                }
        )
        Card(
            modifier = Modifier
                .padding(start = 17.dp, end = 15.dp)
                .align(Alignment.Center),
            colors = CardDefaults.cardColors(containerColor = Color.Black),
            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
            shape = RoundedCornerShape(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp, vertical = 42.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Image inside dialog
                Image(
                    painter = painterResource(id = R.drawable.ic_api_failure),
                    contentDescription = "Failure Icon"
                )

                Spacer(modifier = Modifier.height(21.dp))

                Text(
                    text = "Your Session Expired", // Replace with stringResource(R.string.logout_title) if needed
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFFFFFFF),
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Please login again to continue.", // Replace with stringResource(R.string.logout_body) if needed
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Light,
                    color = Color(0xFFFFFFFF),
                    modifier = Modifier.padding(horizontal = 12.dp),
                    lineHeight = 22.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShowLogoutPopupPreview() {
    val navController = rememberNavController()
    PulsePlayTheme {
        ShowLogoutPopupPreViewable()
    }
}


@Composable
fun ShowLogoutPopupPreViewable(viewModel: AppLogoutViewModel = hiltViewModel(),) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF7E7E7E))
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_close),
            contentDescription = "Close Dialog",
            modifier = Modifier
                .size(30.dp)
                .padding(8.dp)
                .align(Alignment.TopEnd)
                .padding(top = 40.dp, end = 16.dp)
                .clickable {
                    viewModel.goToLogInPage()
                }
        )

        Card(
            modifier = Modifier
                .padding(start = 17.dp, end = 15.dp)
                .align(Alignment.Center),
            colors = CardDefaults.cardColors(containerColor = Color.Black),
            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
            shape = RoundedCornerShape(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp, vertical = 42.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_api_failure),
                    contentDescription = "Failure Icon"
                )

                Spacer(modifier = Modifier.height(21.dp))

                Text(
                    text = "Your Session Expired",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Please login again to continue.",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp),
                    lineHeight = 22.sp
                )
            }
        }
    }
}

