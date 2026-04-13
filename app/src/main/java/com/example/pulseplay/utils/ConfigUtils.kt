package com.example.pulseplay.utils

import com.example.pulseplay.BuildConfig

object ConfigUtils {
    val clientID: String = BuildConfig.SPOTIFY_CLIENT_ID
    val clientSecret: String = BuildConfig.SPOTIFY_CLIENT_SECRET
    const val redirectUri = "com.example.pulseplay://callback"
    const val codeChallengeCode = "S256"
    const val scope = "user-read-email user-read-private"
    const val responseType = "code"
    const val grantType = "authorization_code"
}
