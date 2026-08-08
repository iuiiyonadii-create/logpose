package com.uriel.logpose.features.music

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.features.music.engine.SpotifyRemoteManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * MusicManager V5.6: Con Conexión Proactiva a Spotify (Misión #016.1).
 */
object MusicManager {

    private var context: android.content.Context? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    private var lastBookmark: Pair<String, Long>? = null

    private val _state = MutableStateFlow(MusicState.IDLE)
    val state = _state.asStateFlow()

    private val _volume = MutableStateFlow(0.7f)
    val volume = _volume.asStateFlow()

    fun initialize(context: android.content.Context) {
        this.context = context.applicationContext
        LogPoseLogger.i("MusicManager: Inicializado.")
        
        // Misión #016.1: Intentar conexión proactiva a Spotify
        scope.launch {
            LogPoseLogger.d("MusicManager: Intentando conectar con Spotify Remote...")
            val success = SpotifyRemoteManager.connect(context)
            if (success) {
                LogPoseLogger.i("MusicManager: Spotify Remote vinculado con éxito.")
            } else {
                LogPoseLogger.w("MusicManager: Falló la vinculación con Spotify. ¿La app está abierta?")
            }
        }
    }

    fun play(query: String = "") {
        _state.value = MusicState.MUSIC_PLAYING
        
        if (query.isBlank()) {
            // Intento de reanudación inteligente
            val bookmark = lastBookmark
            if (bookmark != null) {
                LogPoseLogger.i("MusicManager: Reanudando desde marcador: ${bookmark.first}")
                SpotifyRemoteManager.seekAndPlay(bookmark.first, bookmark.second)
            } else {
                SpotifyRemoteManager.resume()
            }
        } else {
            SpotifyRemoteManager.searchAndPlay(query)
        }
    }

    fun pause() {
        _state.value = MusicState.MUSIC_PAUSED
        
        // Misión #016: Guardar marcador antes de pausar físicamente
        lastBookmark = SpotifyRemoteManager.getCurrentBookmark()
        if (lastBookmark != null) {
            LogPoseLogger.d("MusicManager: Marcador guardado: ${lastBookmark?.first} @ ${lastBookmark?.second}ms")
        }
        
        SpotifyRemoteManager.pause()
    }

    fun next() {
        LogPoseLogger.i("MusicManager: Siguiente canción")
        SpotifyRemoteManager.next()
    }

    fun previous() {
        LogPoseLogger.i("MusicManager: Canción anterior")
        SpotifyRemoteManager.previous()
    }

    fun volumeUp() {
        _volume.value = (_volume.value + 0.1f).coerceAtMost(1.0f)
    }

    fun volumeDown() {
        _volume.value = (_volume.value - 0.1f).coerceAtLeast(0.0f)
    }

    fun setVolumeAbsolute(level: Int) {
        _volume.value = (level / 100f).coerceIn(0.0f, 1.0f)
    }

    fun duck() {
        LogPoseLogger.i("MusicManager: Bajando volumen por voz (Duck)")
    }

    fun unduck() {
        LogPoseLogger.i("MusicManager: Restaurando volumen (Unduck)")
    }

    fun setDefaultPlayer(pkg: String) {
        LogPoseLogger.i("MusicManager: Reproductor predeterminado seteado a $pkg")
    }
}
