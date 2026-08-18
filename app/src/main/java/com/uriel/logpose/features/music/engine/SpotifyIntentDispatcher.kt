package com.uriel.logpose.features.music.engine

import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import android.app.SearchManager
import com.uriel.logpose.core.compat.core.LogPoseLogger

import java.net.URLDecoder
import java.nio.charset.StandardCharsets

/**
 * SpotifyIntentDispatcher: Automatización extrema de lanzamientos de música.
 * v88.0: Limpieza robusta con URLDecoder para búsquedas perfectas.
 */
object SpotifyIntentDispatcher {

    fun ejecutarIntentSpotify(context: Context, query: String) {
        try {
            // v88.1: Decodificación Staff Total - Detectar y limpiar URIs o Queries codificados
            val cleanQuery = when {
                query.startsWith("spotify:search:") -> {
                    URLDecoder.decode(query.replace("spotify:search:", ""), StandardCharsets.UTF_8.toString())
                }
                query.contains("%20") || query.contains("%C3") -> {
                    // Si no tiene el prefix pero parece codificado, lo forzamos
                    URLDecoder.decode(query, StandardCharsets.UTF_8.toString())
                }
                else -> query
            }
            
            LogPoseLogger.i("SpotifyDispatcher: Buscando y reproduciendo -> '$cleanQuery'")
            
            val intent = Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH).apply {
                // v97.0: Android Media Standard Focus
                putExtra(SearchManager.QUERY, cleanQuery)
                putExtra(MediaStore.EXTRA_MEDIA_FOCUS, MediaStore.Audio.Media.ENTRY_CONTENT_TYPE)
                putExtra("android.intent.extra.media.autoplay", true)
                
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                `package` = "com.spotify.music"
            }
            
            context.startActivity(intent)
            LogPoseLogger.d("SpotifyDispatcher", "Intent 'Play from Search' enviado: $cleanQuery")
        } catch (e: Exception) {
            LogPoseLogger.e("SpotifyDispatcher: Error lanzando Intent: ${e.message}")
            // Fallback al esquema URI básico (v87.0: Prevención de doble prefijo)
            try {
                val finalUri = if (query.startsWith("spotify:")) query else "spotify:search:$query"
                val fallbackIntent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(finalUri)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallbackIntent)
            } catch (fallbackError: Exception) {
                LogPoseLogger.e("SpotifyDispatcher: Spotify no instalado. Imposible reproducir.")
            }
        }
    }
}
