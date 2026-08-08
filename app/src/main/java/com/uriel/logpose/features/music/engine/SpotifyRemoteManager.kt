package com.uriel.logpose.features.music.engine

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.PlayerState
import kotlinx.coroutines.suspendCancellableCoroutine
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

    suspend fun connect(context: Context): Boolean = suspendCancellableCoroutine { continuation ->
        if (spotifyAppRemote?.isConnected == true) {
            continuation.resume(true)
            return@suspendCancellableCoroutine
        }

        SpotifyAppRemote.connect(context.applicationContext, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(remote: SpotifyAppRemote) {
                spotifyAppRemote = remote
                Log.i(TAG, "Spotify App Remote CONECTADO 🚀")
                
                // Suscribirse al estado para el sistema de marcadores y aprendizaje Staff (v7.6)
                remote.playerApi.subscribeToPlayerState().setEventCallback { state ->
                    lastPlayerState = state
                    
                    // v7.6: Aprendizaje Automático de Preferencias en Tiempo Real
                    state.track?.let { track ->
                        val artistName = track.artist.name
                        val trackName = track.name
                        
                        if (artistName.isNotBlank()) {
                            com.uriel.logpose.thamis.learning.LearningEngine.addFavoriteArtist(artistName)
                            com.uriel.logpose.thamis.learning.LearningEngine.learnMusicEntity(artistName)
                        }
                        if (trackName.isNotBlank()) {
                            com.uriel.logpose.thamis.learning.LearningEngine.learnMusicEntity(trackName)
                        }
                        
                        // v7.8: Vinculación ADN de canción con su artista
                        if (trackName.isNotBlank() && artistName.isNotBlank()) {
                            com.uriel.logpose.thamis.learning.LearningEngine.learnTrackArtistRelation(trackName, artistName)
                        }
                    }
                }

                processQueue()
                scanLibraryAffinities() // v7.6: Escaneo inicial de gustos
                
                if (continuation.isActive) continuation.resume(true)
            }

            override fun onFailure(throwable: Throwable) {
                Log.e(TAG, "Spotify Remote Error: ${throwable.message}")
                if (continuation.isActive) continuation.resume(false)
            }
        })
    }

    private fun processQueue() {
        synchronized(commandQueue) {
            Log.d(TAG, "Spotify: Procesando cola de comandos (${commandQueue.size} pendientes)")
            commandQueue.forEach { it.invoke() }
            commandQueue.clear()
        }
    }

    fun play(uri: String) {
        if (spotifyAppRemote?.isConnected == true) {
            spotifyAppRemote?.playerApi?.play(uri)
        } else {
            Log.w(TAG, "Spotify: Comando 'play' encolado (esperando conexión)")
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
            Log.i(TAG, "Marcador restaurado: $uri en ${positionMs}ms")
        }
    }

    fun searchAndPlay(query: String) {
        if (spotifyAppRemote?.isConnected == true) {
            Log.d(TAG, "Spotify: Ejecutando búsqueda remota para '$query'")
            // Intentamos despertar al motor de búsqueda
            spotifyAppRemote?.contentApi?.getRecommendedContentItems("default")?.setResultCallback { items ->
                Log.d(TAG, "Spotify: Motor despertado (${items.items.size} recomendaciones)")
            }
            
            val searchUri = "spotify:search:${Uri.encode(query)}"
            spotifyAppRemote?.playerApi?.play(searchUri)
        } else {
            Log.w(TAG, "Spotify: No conectado. Usando Intent de respaldo.")
            launchSpotifySearchIntent(query)
        }
    }

    /**
     * v7.6: Escanea las recomendaciones de Spotify para inyectar gustos del usuario
     * en la red neuronal de THAMIS de forma proactiva.
     */
    private fun scanLibraryAffinities() {
        if (spotifyAppRemote?.isConnected != true) return

        // 1. Escaneo de Recomendaciones y Temas
        spotifyAppRemote?.contentApi?.getRecommendedContentItems("default")?.setResultCallback { result ->
            Log.i(TAG, "🧠 Sincronizando gustos Staff: ${result.items.size} elementos encontrados.")
            result.items.forEach { item ->
                if (item.playable) {
                    val title = item.title ?: ""
                    val subtitle = item.subtitle ?: "" // Suele ser el artista
                    
                    if (subtitle.isNotBlank()) {
                        com.uriel.logpose.thamis.learning.LearningEngine.addFavoriteArtist(subtitle)
                        com.uriel.logpose.thamis.learning.LearningEngine.learnMusicEntity(subtitle)
                    }
                    if (title.isNotBlank()) {
                        com.uriel.logpose.thamis.learning.LearningEngine.learnMusicEntity(title)
                    }
                    
                    // v7.8: Sincronización inicial de ADN de biblioteca
                    if (title.isNotBlank() && subtitle.isNotBlank()) {
                        com.uriel.logpose.thamis.learning.LearningEngine.learnTrackArtistRelation(title, subtitle)
                    }
                }
            }
        }

        // 2. v7.9: Escaneo de Playlists Privadas
        spotifyAppRemote?.contentApi?.getRecommendedContentItems("fitness")?.setResultCallback { result ->
            result.items.forEach { item ->
                if (!item.playable && item.title.isNotBlank()) {
                    com.uriel.logpose.thamis.learning.LearningEngine.learnPlaylist(item.title)
                }
            }
        }
    }

    private fun launchSpotifySearchIntent(query: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("spotify:search:${Uri.encode(query)}")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            com.uriel.logpose.core.app.LogPoseApplication.instance.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Spotify Intent Error: ${e.message}")
        }
    }

    fun disconnect() {
        if (spotifyAppRemote != null) {
            SpotifyAppRemote.disconnect(spotifyAppRemote)
            spotifyAppRemote = null
        }
    }
}
