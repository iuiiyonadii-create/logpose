package com.uriel.logpose.features.music.engine

import android.content.Context
import android.net.Uri
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.compat.core.AppContextProvider
import com.uriel.logpose.core.services.MusicEntityDelegator
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.PlayerState
import kotlinx.coroutines.*
import kotlin.coroutines.resume

/**
 * SpotifyRemoteManager v1.3: Con Búsqueda de Respaldo y Wake-Up (Misión #016.2).
 */
object SpotifyRemoteManager {
    private const val TAG = "SpotifyRemote"
    private var spotifyAppRemote: SpotifyAppRemote? = null

    private val connectionParams = ConnectionParams.Builder(SpotifyConfiguration.CLIENT_ID)
        .setRedirectUri(SpotifyConfiguration.REDIRECT_URI)
        .showAuthView(true) 
        .build()

    // Último estado conocido (Marcador en memoria)
    private var lastPlayerState: PlayerState? = null
    private val commandQueue = mutableListOf<() -> Unit>()
    private var kickstartJob: Job? = null

    fun isConnected(): Boolean = spotifyAppRemote?.isConnected == true

    suspend fun connect(context: Context): Boolean = suspendCancellableCoroutine { continuation ->
        if (spotifyAppRemote?.isConnected == true) {
            continuation.resume(true)
            return@suspendCancellableCoroutine
        }

        SpotifyAppRemote.connect(context.applicationContext, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(remote: SpotifyAppRemote) {
                spotifyAppRemote = remote
                LogPoseLogger.i(TAG, "Spotify App Remote CONECTADO 🚀")

                remote.playerApi.subscribeToPlayerState().setEventCallback { state ->
                    lastPlayerState = state
                    
                    state.track?.let { track ->
                        val artist = track.artist.name
                        val title = track.name
                        if (title.isNotBlank()) {
                            MusicEntityDelegator.learnVectorMemory(title, "Canción de $artist")
                        }
                    }
                }

                processQueue()
                scanLibraryAffinities()

                if (continuation.isActive) continuation.resume(true)
            }

            override fun onFailure(throwable: Throwable) {
                LogPoseLogger.e(TAG, "Spotify Remote Error: ${throwable.message}")
                if (continuation.isActive) continuation.resume(false)
            }
        })
    }

    private fun processQueue() {
        synchronized(commandQueue) {
            LogPoseLogger.d(TAG, "Spotify: Procesando cola de comandos (${commandQueue.size} pendientes)")
            commandQueue.forEach { it.invoke() }
            commandQueue.clear()
        }
    }

    fun play(uri: String) {
        if (spotifyAppRemote?.isConnected == true) {
            spotifyAppRemote?.playerApi?.play(uri)
        } else {
            LogPoseLogger.w(TAG, "Spotify: Comando 'play' encolado (esperando conexión)")
            synchronized(commandQueue) { commandQueue.add { spotifyAppRemote?.playerApi?.play(uri) } }
        }
    }

    fun resume() {
        if (spotifyAppRemote?.isConnected == true) {
            spotifyAppRemote?.playerApi?.resume()
        } else {
            synchronized(commandQueue) { commandQueue.add { spotifyAppRemote?.playerApi?.resume() } }
        }
    }

    fun pause() {
        if (spotifyAppRemote?.isConnected == true) {
            spotifyAppRemote?.playerApi?.pause()
        }
    }

    fun next() {
        if (spotifyAppRemote?.isConnected == true) {
            spotifyAppRemote?.playerApi?.skipNext()
        }
    }

    fun previous() {
        if (spotifyAppRemote?.isConnected == true) {
            spotifyAppRemote?.playerApi?.skipPrevious()
        }
    }

    /**
     * Devuelve el marcador actual (URI + Posición).
     */
    fun getCurrentBookmark(): Pair<String, Long>? {
        val state = lastPlayerState ?: return null
        val uri = state.track?.uri ?: return null
        return Pair(uri, state.playbackPosition)
    }

    /**
     * Reanuda la reproducción desde un marcador específico.
     */
    fun seekAndPlay(uri: String, positionMs: Long) {
        if (spotifyAppRemote?.isConnected == true) {
            spotifyAppRemote?.playerApi?.play(uri)
            spotifyAppRemote?.playerApi?.seekTo(positionMs)
            LogPoseLogger.i(TAG, "Marcador restaurado: $uri en ${positionMs}ms")
        }
    }

    fun searchAndPlay(query: String) {
        val rawQuery = query.replace("spotify:search:", "").trim()
        val cleanQuery = try {
            java.net.URLDecoder.decode(rawQuery, java.nio.charset.StandardCharsets.UTF_8.toString())
        } catch (_: Exception) { rawQuery }

        if (spotifyAppRemote?.isConnected == true) {
            if (cleanQuery.isBlank()) {
                LogPoseLogger.d(TAG, "Spotify: Reanudación inteligente (Recomendaciones Staff)")
                spotifyAppRemote?.contentApi?.getRecommendedContentItems("default")?.setResultCallback { result ->
                    val firstPlayable = result.items.find { it.playable }
                    if (firstPlayable != null) {
                        spotifyAppRemote?.playerApi?.play(firstPlayable.uri)
                    } else {
                        spotifyAppRemote?.playerApi?.resume()
                    }
                }
            } else {
                LogPoseLogger.d(TAG, "Spotify: Lanzando búsqueda profunda para '$cleanQuery'")
                kickstartJob?.cancel()
                launchSpotifySearchIntent(cleanQuery)
                
                kickstartJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(3000)
                    if (isConnected()) {
                        LogPoseLogger.d(TAG, "Kickstart [1/3]: SDK Resume")
                        spotifyAppRemote?.playerApi?.resume()
                        
                        delay(1000)
                        LogPoseLogger.d(TAG, "Kickstart [2/3]: Hardware Key Injection")
                        HardwareMediaController.executeCommand(AppContextProvider.applicationContext, "PLAY")
                    }
                }
            }
        } else {
            LogPoseLogger.w(TAG, "Spotify: No conectado. Usando Intent de respaldo.")
            launchSpotifySearchIntent(cleanQuery)
        }
    }

    private fun scanLibraryAffinities() {
        spotifyAppRemote?.contentApi?.getRecommendedContentItems("default")?.setResultCallback { result ->
            result.items.forEach { item ->
                if (item.playable) {
                    val title = item.title ?: ""
                    val subtitle = item.subtitle ?: ""
                    
                    if (subtitle.isNotBlank()) {
                        MusicEntityDelegator.addFavoriteArtist(subtitle)
                        MusicEntityDelegator.learnMusicEntity(subtitle)
                    }
                    if (title.isNotBlank()) {
                        MusicEntityDelegator.learnMusicEntity(title)
                    }
                    
                    if (title.isNotBlank() && subtitle.isNotBlank()) {
                        MusicEntityDelegator.learnTrackArtistRelation(title, subtitle)
                    }
                }
            }
        }

        spotifyAppRemote?.contentApi?.getRecommendedContentItems("fitness")?.setResultCallback { result ->
            result.items.forEach { item ->
                if (!item.playable && item.title.isNotBlank()) {
                    MusicEntityDelegator.learnPlaylist(item.title)
                }
            }
        }
    }

    private fun launchSpotifySearchIntent(query: String) {
        SpotifyIntentDispatcher.ejecutarIntentSpotify(AppContextProvider.applicationContext, query)
    }

    fun disconnect() {
        if (spotifyAppRemote != null) {
            SpotifyAppRemote.disconnect(spotifyAppRemote)
            spotifyAppRemote = null
        }
    }
}
