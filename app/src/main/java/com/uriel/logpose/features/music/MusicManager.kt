package com.uriel.logpose.features.music

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.features.music.engine.SpotifyRemoteManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import android.media.AudioManager
import android.content.Context

/**
 * MusicManager V5.6: Con Conexión Proactiva a Spotify (Misión #016.1).
 */
object MusicManager {

    private var context: Context? = null
    // v69.2: Scope de Main absoluto para evitar conflictos con el SDK de Spotify
    private val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())
    
    private var lastBookmark: Pair<String, Long>? = null

    private val _state = MutableStateFlow(MusicState.IDLE)
    val state = _state.asStateFlow()

    private val _volume = MutableStateFlow(0.7f)
    val volume = _volume.asStateFlow()
    private var baseVolume = 0.7f
    private var isDucked = false
    private var transitionJob: Job? = null

    fun initialize(context: Context) {
        if (this.context != null) return // v88.1: Evitar doble inicialización Staff
        
        this.context = context.applicationContext
        
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

        // v72.0: Sincronización de flujo de volumen con el hardware del sistema
        volume.onEach { vol ->
            val targetLevel = (vol * maxVolume).toInt()
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetLevel, 0)
        }.launchIn(scope)

        // v69.2: Forzamos el hilo principal de forma inmediata
        scope.launch {
            LogPoseLogger.d("MusicManager: Intentando conectar con Spotify Remote (Main Thread Check)...")
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
        
        // v85.0: Auto-reconexión Staff si el Remote se cayó
        if (!SpotifyRemoteManager.isConnected()) {
            context?.let { ctx ->
                LogPoseLogger.w("MusicManager: Detectada desconexión de Spotify. Intentando re-vincular...")
                scope.launch {
                    val reconnected = SpotifyRemoteManager.connect(ctx)
                    if (reconnected) {
                        LogPoseLogger.i("MusicManager: Re-vínculo exitoso.")
                        executePlay(query)
                    } else {
                        LogPoseLogger.e("MusicManager: No se pudo re-vincular. Usando Intent de respaldo.")
                        executePlay(query)
                    }
                }
                return
            }
        }
        
        executePlay(query)
    }

    private fun executePlay(query: String) {
        if (query.isBlank()) {
            // Intento de reanudación inteligente
            val bookmark = lastBookmark
            if (bookmark != null) {
                LogPoseLogger.i("MusicManager: Reanudando desde marcador: ${bookmark.first}")
                SpotifyRemoteManager.seekAndPlay(bookmark.first, bookmark.second)
            } else {
                // v88.0: Si el Remote falla, intentamos reanudar vía Intent vacío
                if (!SpotifyRemoteManager.isConnected()) {
                    LogPoseLogger.d("MusicManager", "Remote offline. Intentando reanudación vía Intent.")
                    SpotifyRemoteManager.searchAndPlay("") 
                } else {
                    SpotifyRemoteManager.resume()
                }
            }
        } else {
            // v87.0: Detección de URI Staff para evitar doble codificación
            if (query.startsWith("spotify:")) {
                LogPoseLogger.d("MusicManager", "Comando URI detectado: $query")
                
                // v88.0: Si es búsqueda, usamos searchAndPlay para validación profunda
                if (query.contains(":search:")) {
                    SpotifyRemoteManager.searchAndPlay(query)
                } else {
                    SpotifyRemoteManager.play(query)
                }
            } else {
                SpotifyRemoteManager.searchAndPlay(query)
            }
        }
    }

    fun pause() {
        _state.value = MusicState.MUSIC_PAUSED
        
        if (!SpotifyRemoteManager.isConnected()) {
            context?.let { ctx ->
                scope.launch { SpotifyRemoteManager.connect(ctx); executePause() }
                return
            }
        }
        executePause()
    }

    private fun executePause() {
        // Misión #016: Guardar marcador antes de pausar físicamente
        lastBookmark = SpotifyRemoteManager.getCurrentBookmark()
        if (lastBookmark != null) {
            LogPoseLogger.d("MusicManager: Marcador guardado: ${lastBookmark?.first} @ ${lastBookmark?.second}ms")
        }
        SpotifyRemoteManager.pause()
    }

    fun next() {
        LogPoseLogger.i("MusicManager: Siguiente canción")
        if (!SpotifyRemoteManager.isConnected()) {
            context?.let { ctx ->
                scope.launch { SpotifyRemoteManager.connect(ctx); SpotifyRemoteManager.next() }
                return
            }
        }
        SpotifyRemoteManager.next()
    }

    fun previous() {
        LogPoseLogger.i("MusicManager: Canción anterior")
        if (!SpotifyRemoteManager.isConnected()) {
            context?.let { ctx ->
                scope.launch { SpotifyRemoteManager.connect(ctx); SpotifyRemoteManager.previous() }
                return
            }
        }
        SpotifyRemoteManager.previous()
    }

    fun volumeUp() {
        baseVolume = (baseVolume + 0.1f).coerceAtMost(1.0f)
        if (!isDucked) _volume.value = baseVolume
    }

    fun volumeDown() {
        baseVolume = (baseVolume - 0.1f).coerceAtLeast(0.0f)
        if (!isDucked) _volume.value = baseVolume
    }

    fun setVolumeAbsolute(level: Int) {
        baseVolume = (level / 100f).coerceIn(0.0f, 1.0f)
        if (!isDucked) {
            _volume.value = baseVolume
        }
    }

    fun duck() {
        if (isDucked) return
        isDucked = true
        LogPoseLogger.i("MusicManager: Bajando volumen por voz (Duck)")
        
        transitionJob?.cancel()
        transitionJob = scope.launch {
            val steps = 5
            val decrement = (baseVolume - 0.2f) / steps
            for (i in 1..steps) {
                if (!isActive) break
                _volume.value = (baseVolume - decrement * i).coerceAtLeast(0.2f)
                delay(40) 
            }
        }
    }

    fun unduck() {
        if (!isDucked) return
        isDucked = false
        LogPoseLogger.i("MusicManager: Restaurando volumen (Unduck)")
        
        transitionJob?.cancel()
        transitionJob = scope.launch {
            val steps = 8
            val current = _volume.value
            val increment = (baseVolume - current) / steps
            for (i in 1..steps) {
                if (!isActive) break
                _volume.value = (current + increment * i).coerceAtMost(baseVolume)
                delay(60)
            }
        }
    }

    fun setDefaultPlayer(pkg: String) {
        LogPoseLogger.i("MusicManager: Reproductor predeterminado seteado a $pkg")
    }
}
