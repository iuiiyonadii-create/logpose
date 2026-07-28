package com.uriel.logpose.features.music.engine

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Gestor de Autenticación v7.0: Ahora con StateFlow para sincronizar con el MusicManager.
 */
object SpotifyAuthManager {
    private const val TAG = "SpotifyAuth"
    const val AUTH_TOKEN_REQUEST_CODE = 1337
    
    private val _tokenFlow = MutableStateFlow<String?>(null)
    val tokenFlow: StateFlow<String?> = _tokenFlow

    private const val PREF_NAME = "spotify_prefs"
    private const val KEY_TOKEN = "access_token"

    fun init(context: android.content.Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, android.content.Context.MODE_PRIVATE)
        val savedToken = prefs.getString(KEY_TOKEN, null)
        if (savedToken != null) {
            _tokenFlow.value = savedToken
            Log.d(TAG, "Token recuperado de memoria.")
        }
    }

    fun login(activity: Activity) {
        Log.d(TAG, "Iniciando flujo de login Spotify (v5.0)...")
        if (_tokenFlow.value != null) {
            Log.d(TAG, "Ya existe un token activo.")
            return
        }

        val request = AuthorizationRequest.Builder(
            SpotifyConfiguration.CLIENT_ID,
            AuthorizationResponse.Type.TOKEN,
            SpotifyConfiguration.REDIRECT_URI
        ).setScopes(SpotifyConfiguration.SCOPES)
            .build()

        AuthorizationClient.openLoginActivity(activity, AUTH_TOKEN_REQUEST_CODE, request)
    }

    fun onAuthResponse(context: android.content.Context, resultCode: Int, data: Intent?) {
        val response = AuthorizationClient.getResponse(resultCode, data)
        when (response.type) {
            AuthorizationResponse.Type.TOKEN -> {
                val token = response.accessToken
                _tokenFlow.value = token
                
                // Persistencia local
                context.getSharedPreferences(PREF_NAME, android.content.Context.MODE_PRIVATE)
                    .edit()
                    .putString(KEY_TOKEN, token)
                    .apply()
                
                Log.i(TAG, "Token obtenido y guardado con éxito.")
            }
            AuthorizationResponse.Type.ERROR -> {
                Log.e(TAG, "Error de Spotify Auth: ${response.error}")
            }
            else -> Log.w(TAG, "Login cancelado.")
        }
    }

    /**
     * Espera de forma suspendida hasta que el token esté disponible.
     */
    suspend fun awaitToken(timeoutMs: Long = 5000): String? {
        return withTimeoutOrNull(timeoutMs) {
            tokenFlow.first { it != null }
        }
    }

    fun getAccessToken(): String? = _tokenFlow.value
}
