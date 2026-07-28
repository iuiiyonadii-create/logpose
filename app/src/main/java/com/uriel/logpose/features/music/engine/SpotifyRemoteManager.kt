package com.uriel.logpose.features.music.engine

import android.content.Context
import android.util.Log
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * SpotifyRemoteManager v1.1: Conexión blindada con Timeout y diagnóstico AIDL.
 */
object SpotifyRemoteManager {
    private const val TAG = "SpotifyRemote"
    private var spotifyAppRemote: SpotifyAppRemote? = null

    private val connectionParams = ConnectionParams.Builder(SpotifyConfiguration.CLIENT_ID)
        .setRedirectUri(SpotifyConfiguration.REDIRECT_URI)
        .showAuthView(true) // Forzamos vista para asegurar vinculación en HyperOS
        .build()

    suspend fun connect(context: Context): Boolean = suspendCancellableCoroutine { continuation ->
        if (spotifyAppRemote?.isConnected == true) {
            Log.d(TAG, "Spotify Remote ya estaba conectado")
            continuation.resume(true)
            return@suspendCancellableCoroutine
        }

        Log.d(TAG, "Nivel 1: Intentando App Remote...")
        var finished = false
        val timeoutHandler = android.os.Handler(android.os.Looper.getMainLooper())
        
        val timeoutRunnable = Runnable {
            if (!finished && continuation.isActive) {
                finished = true
                Log.e(TAG, "TIMEOUT: Spotify App Remote no respondió en 8 segundos (Handshake bloqueado)")
                continuation.resume(false)
            }
        }

        timeoutHandler.postDelayed(timeoutRunnable, 8000)

        SpotifyAppRemote.connect(context.applicationContext, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(remote: SpotifyAppRemote) {
                if (finished) return
                finished = true
                timeoutHandler.removeCallbacks(timeoutRunnable)
                spotifyAppRemote = remote
                Log.i(TAG, "Spotify App Remote CONECTADO 🚀")
                if (continuation.isActive) continuation.resume(true)
            }

            override fun onFailure(throwable: Throwable) {
                if (finished) return
                finished = true
                timeoutHandler.removeCallbacks(timeoutRunnable)
                Log.e(TAG, "Spotify Remote Error: ${throwable.message}")
                if (continuation.isActive) continuation.resume(false)
            }
        })

        continuation.invokeOnCancellation {
            timeoutHandler.removeCallbacks(timeoutRunnable)
            // No desconectamos el global aquí, solo limpiamos el timer de esta petición
        }
    }

    fun play(uri: String) {
        if (spotifyAppRemote?.isConnected == true) {
            spotifyAppRemote?.playerApi?.play(uri)
                ?.setResultCallback { Log.d(TAG, "Reproduciendo vía Remote: $uri") }
                ?.setErrorCallback { e -> Log.e(TAG, "Error en PlayerApi: ${e.message}") }
        } else {
            Log.w(TAG, "play() ignorado: Remote no conectado.")
        }
    }

    /**
     * Búsqueda proactiva: Intenta reproducir el término directamente.
     */
    fun searchAndPlay(query: String) {
        val searchUri = "spotify:search:${android.net.Uri.encode(query)}"
        play(searchUri)
    }

    fun disconnect() {
        if (spotifyAppRemote != null) {
            SpotifyAppRemote.disconnect(spotifyAppRemote)
            spotifyAppRemote = null
            Log.d(TAG, "Spotify Remote desconectado manualmente.")
        }
    }
}
