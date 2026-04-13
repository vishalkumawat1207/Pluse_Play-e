package com.example.pulseplay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
//import androidx.lifecycle.testing.TestLifecycleOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.compose.rememberNavController
import com.example.pulseplay.navigation.AppNavGraph
import com.example.pulseplay.navigation.Screen
import com.example.pulseplay.ui.theme.PulsePlayTheme
import com.example.pulseplay.utils.ConfigUtils
import com.example.pulseplay.utils.PreferenceUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intentUri = intent?.data

        setContent {
            PulsePlayTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    var startDestination by remember { mutableStateOf<String?>(null) }
                    val context = LocalContext.current
                    var shouldRedirect by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        val loginStatus = PreferenceUtils.getBoolData(context, PreferenceUtils.loginStatus)
                        startDestination = if (loginStatus) Screen.Home.route else Screen.Login.route

                        // Handle redirect if deep link present
                        if (intentUri != null && intentUri.toString().startsWith(ConfigUtils.redirectUri)) {
                            shouldRedirect = true
                        }
                    }

                    // Only show NavGraph once startDestination is known
                    startDestination?.let { dest ->
                        AppNavGraph(navController = navController, startDestination = dest)
                        LaunchedEffect(shouldRedirect, dest) {
                            if (shouldRedirect) {
                                navController.navigate(Screen.Redirect.route) {
                                    popUpTo(dest) { inclusive = true }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = false, apiLevel = 33)
@Composable
fun GreetingPreview() {
    val viewModelStoreOwner = object : ViewModelStoreOwner { // <--- HERE: Implement ViewModelStoreOwner
        override val viewModelStore = ViewModelStore() // <--- HERE: ViewModelStore from lifecycle library
    }

    CompositionLocalProvider(
//        LocalLifecycleOwner provides lifecycleOwner, // Correct import for LocalLifecycleOwner
        LocalViewModelStoreOwner provides viewModelStoreOwner
    ) {
        val navController = rememberNavController()
        PulsePlayTheme {
            // Ensure AppNavGraph is defined and takes NavController
            AppNavGraph(navController = navController)
        }
    }
}