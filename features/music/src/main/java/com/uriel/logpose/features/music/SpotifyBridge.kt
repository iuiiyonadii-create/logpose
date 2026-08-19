package com.uriel.logpose.features.music

import android.content.Context
import android.media.MediaMetadata
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.services.SpotifyControllerDelegator
import com.uriel.logpose.features.music.model.SpotifyConnectionState
import java.text.Normalizer

/**
 * SpotifyBridge v9.0: Integración Universal vía MediaController.
 * No requiere SDKs pesados, usa el sistema de notificaciones de Android.
 */
class SpotifyBridge(private val context: Context) {

    private var connectionState = SpotifyConnectionState.DISCONNECTED

    companion object {
        private const val TAG = "SpotifyBridge"
        private val STOPWORDS = setOf("el", "la", "los", "las", "de", "del", "y", "ft", "feat", "pone", "pon", "reproducir", "reproduce")
    }

    /**
     * SINCRO: Verifica si lo que suena coincide con lo que pidió el usuario.
     */
    fun isPlayingQuery(query: String): Boolean {
        val controller = SpotifyControllerDelegator.getSpotifyController(context)
        if (controller == null) {
            if (connectionState != SpotifyConnectionState.CONNECTING) {
                LogPoseLogger.e(TAG, "JUEZ: No hay controlador. Intentando despertar servicio...")
                connectionState = SpotifyConnectionState.CONNECTING
                SpotifyControllerDelegator.tryForceRebind(context)
            } else {
                LogPoseLogger.d(TAG, "THAMIS: Música no disponible (esperando rebind)")
            }
            return false
        }
        
        connectionState = SpotifyConnectionState.CONNECTED
        val metadata = controller.metadata
        if (metadata == null) {
            LogPoseLogger.w(TAG, "JUEZ: Spotify está conectado pero no hay metadata aún.")
            return false
        }
        
        val title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE) ?: ""
        val artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST) ?: ""
        
        val actual = tokenize("$title $artist")
        val expected = tokenize(query)
        
        val match = fuzzyMatchTokens(expected, actual)
        if (match) {
            LogPoseLogger.i(TAG, "JUEZ: ¡MATCH CONFIRMADO! Suena: $title - $artist")
        } else {
            LogPoseLogger.d(TAG, "JUEZ: No coincide. Esperaba '$query', suena '$title - $artist'")
        }
        return match
    }

    private fun fuzzyMatchTokens(expected: List<String>, actual: List<String>): Boolean {
        if (expected.isEmpty() || actual.isEmpty()) return false
        var matched = 0
        for (et in expected) {
            val bestSim = actual.maxOfOrNull { similarity(et, it) } ?: 0.0
            if (bestSim >= 0.7) matched++
        }
        return (matched.toDouble() / expected.size) >= 0.4
    }

    private fun similarity(a: String, b: String): Double {
        val maxLen = maxOf(a.length, b.length)
        if (maxLen == 0) return 1.0
        return 1.0 - levenshtein(a, b).toDouble() / maxLen
    }

    private fun tokenize(s: String): List<String> = s.lowercase()
        .let { Normalizer.normalize(it, Normalizer.Form.NFD) }
        .replace(Regex("\\p{M}"), "")
        .replace(Regex("[^a-z0-9 ]"), "")
        .split(" ")
        .filter { it.isNotBlank() && it !in STOPWORDS }

    private fun levenshtein(a: String, b: String): Int {
        val dp = Array(a.length + 1) { IntArray(b.length + 1) }
        for (i in 0..a.length) dp[i][0] = i
        for (j in 0..b.length) dp[0][j] = j
        for (i in 1..a.length) {
            for (j in 1..b.length) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                dp[i][j] = minOf(dp[i - 1][j] + 1, dp[i][j - 1] + 1, dp[i - 1][j - 1] + cost)
            }
        }
        return dp[a.length][b.length]
    }

    // --- CONTROLES DE REPRODUCCIÓN ---
    fun resume() {
        if (connectionState != SpotifyConnectionState.CONNECTED) {
            LogPoseLogger.w(TAG, "THAMIS: Música no disponible para RESUME")
            return
        }
        SpotifyControllerDelegator.getSpotifyController(context)?.transportControls?.play() 
    }

    fun pause() {
        if (connectionState != SpotifyConnectionState.CONNECTED) {
            LogPoseLogger.w(TAG, "THAMIS: Música no disponible para PAUSE")
            return
        }
        SpotifyControllerDelegator.getSpotifyController(context)?.transportControls?.pause() 
    }

    fun next() {
        if (connectionState != SpotifyConnectionState.CONNECTED) {
            LogPoseLogger.w(TAG, "THAMIS: Música no disponible para NEXT")
            return
        }
        SpotifyControllerDelegator.getSpotifyController(context)?.transportControls?.skipToNext() 
    }

    fun previous() {
        if (connectionState != SpotifyConnectionState.CONNECTED) {
            LogPoseLogger.w(TAG, "THAMIS: Música no disponible para PREVIOUS")
            return
        }
        SpotifyControllerDelegator.getSpotifyController(context)?.transportControls?.skipToPrevious() 
    }
    
    // Método para compatibilidad con MusicManager viejo
    suspend fun connect(): Boolean = true
}
