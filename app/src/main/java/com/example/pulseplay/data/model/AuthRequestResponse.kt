package com.example.pulseplay.data.model

data class AuthRequestResponse(
    val clientId: String,
    val responseType: String,
    val redirectUri: String,
    val codeChallengeMethod: String,
    val codeChallenge: String,
    val scope: String,
)
