package com.example.pulseplay.ui.screen.handler

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pulseplay.navigation.Screen
import com.example.pulseplay.utils.ConfigUtils
import com.example.pulseplay.utils.PreferenceUtils
import com.example.pulseplay.ui.viewModel.auth.AuthViewModel

@Composable
fun RedirectHandler(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current as Activity
    val intent = context.intent
    val uri = intent.data
    val accessToken by viewModel.accessToken.observeAsState()
    val TAG = "RedirectHandler"

    LaunchedEffect(uri) {
        uri?.let {
            Log.d(TAG, "uri : $uri")
            if (it.toString().startsWith(ConfigUtils.redirectUri)) {
                val code = it.getQueryParameter("code")
                if (code != null) {
                    viewModel.getAccessToken(context,code)
                }
            }
        }
    }

    LaunchedEffect(accessToken) {
        if (!accessToken.isNullOrEmpty()) {
            Log.d(TAG, "uri : $uri")
            if (PreferenceUtils.getBoolData(context, PreferenceUtils.loginStatus)) {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Redirect.route) { inclusive = true }
                }
            } else {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Redirect.route) { inclusive = true }
                }
            }

        }
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
