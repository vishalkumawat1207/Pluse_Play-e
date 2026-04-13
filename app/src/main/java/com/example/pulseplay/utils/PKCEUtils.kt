package com.example.pulseplay.utils

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom

object PKCEUtils {

    @JvmStatic
    fun generateCodeVerifier(): String {
        val random = ByteArray(32)
        SecureRandom().nextBytes(random)
        return Base64.encodeToString(random, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }

    @JvmStatic
    fun generateCodeChallenge(codeVerifier: String): String {
        val bytes = codeVerifier.toByteArray()
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val digest = messageDigest.digest(bytes)
        return Base64.encodeToString(digest, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }
}