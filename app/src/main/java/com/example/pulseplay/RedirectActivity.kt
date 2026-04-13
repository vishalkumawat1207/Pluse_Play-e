package com.example.pulseplay

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.pulseplay.utils.ConfigUtils
import com.example.pulseplay.utils.PKCEUtils
import com.example.pulseplay.utils.PreferenceUtils
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class RedirectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_redirect)
        val uri = intent?.data
        if (uri != null && uri.toString().startsWith(ConfigUtils.redirectUri)) {
            Log.d("Spotify", "Uri RedirectActivity : $uri")
            val code = uri.getQueryParameter("code")
            if (code != null) {
                PreferenceUtils.getData(this, PreferenceUtils.codeVerifier)
                    ?.let { exchangeCodeForToken(code, codeVerifier = it) }
            }
        }

        finish()
    }

    private fun exchangeCodeForToken(code: String, codeVerifier: String) {
        val requestBody = FormBody.Builder()
            .add("grant_type", "authorization_code")
            .add("code", code)
            .add("redirect_uri", ConfigUtils.redirectUri)
            .add("client_id", ConfigUtils.clientID)
            .add("code_verifier", codeVerifier)
            .build()

        val request = Request.Builder()
            .url("https://accounts.spotify.com/api/token")
            .post(requestBody)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val json = JSONObject(body ?: "")
                Log.d("Spotify", "Access Token json: $json")
                val accessToken = json.getString("access_token")
                PreferenceUtils.setData(this@RedirectActivity, PreferenceUtils.accessToken,accessToken)
                Log.d("Spotify", "Access Token: $accessToken")
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("Spotify", "Token exchange failed", e)
            }
        })
    }

}