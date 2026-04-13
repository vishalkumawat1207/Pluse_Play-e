package com.example.pulseplay.ui.viewModel.auth

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulseplay.data.model.Artist
import com.example.pulseplay.data.repository.PulsePlayRepository
import com.example.pulseplay.utils.ConfigUtils
import com.example.pulseplay.utils.PKCEUtils
import com.example.pulseplay.utils.PreferenceUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private var repository: PulsePlayRepository,
    private var context: Application,
) : ViewModel() {
    private var _authUri = MutableLiveData<Uri>()
    var authUrl: LiveData<Uri> = _authUri
    private var codeVerifier = PKCEUtils.generateCodeVerifier()
    private var codeChallenge = PKCEUtils.generateCodeChallenge(codeVerifier)
    private var _accessToken = MutableLiveData<String>()
    var accessToken: LiveData<String> = _accessToken
    private val TAG = "AuthViewModel"


    fun startAuthFlow(context: Context) {
        codeVerifier = PKCEUtils.generateCodeVerifier()
        codeChallenge = PKCEUtils.generateCodeChallenge(codeVerifier)
        PreferenceUtils.setData(context, PreferenceUtils.codeVerifier, codeVerifier)

        val authUri = Uri.parse("https://accounts.spotify.com/authorize")
            .buildUpon()
            .appendQueryParameter("client_id", ConfigUtils.clientID)
            .appendQueryParameter("response_type", ConfigUtils.responseType)
            .appendQueryParameter("redirect_uri", ConfigUtils.redirectUri)
            .appendQueryParameter("code_challenge_method", ConfigUtils.codeChallengeCode)
            .appendQueryParameter("code_challenge", codeChallenge)
            .appendQueryParameter("scope", ConfigUtils.scope) // add more scopes if needed
            .build()

        _authUri.value = authUri
    }

    suspend fun getAccessToken(context: Context, code: String) {
        try {
            codeVerifier = PreferenceUtils.getData(context, PreferenceUtils.codeVerifier).toString()
            val response = repository.exchangeCodeForToken(code, codeVerifier)
            PreferenceUtils.setData(context, PreferenceUtils.accessToken, response.accessToken)
            PreferenceUtils.setBoolData(context, PreferenceUtils.loginStatus, true)
            _accessToken.value = response.accessToken
            Log.d(TAG, "Token: ${response.accessToken}")
        } catch (e: HttpException) {
            when (e.code()) {
                400 -> {
                    Log.e(TAG, "HTTP 400: Bad Request - Invalid code or verifier.")
                }

                401 -> {
                    Log.e(TAG, "HTTP 401: Unauthorized - Invalid credentials.")
                }

                else -> {
                    Log.e(TAG, "HTTp ${e.code()} : ${e.message()}")
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error: ${e.localizedMessage}")
        }

    }

}