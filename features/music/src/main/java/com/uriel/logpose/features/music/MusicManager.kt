package com.uriel.logpose.features.music

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.features.music.engine.SpotifyRemoteManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import android.media.AudioManager
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * MusicManager V8.0: Arquitectura DI (Misión #115).
 */
@Singleton
class MusicManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private var instance: MusicManager? = null

        fun get(): MusicManager? = instance

        val state: StateFlow<MusicState>
            get() = instance?.state ?: MutableStateFlow(MusicState.IDLE).asStateFlow()

        val volume: StateFlow<Float>
            get() = instance?.volume ?: MutableStateFlow(0.7f).asStateFlow()

        fun play(query: String = "") {
            instance?.play(query)
        }

        fun pause() {
            instance?.pause()
        }

        fun next() {
            instance?.next()
        }

        fun previous() {
            instance?.previous()
        }

        fun volumeUp() {
            instance?.volumeUp()
        }

        fun volumeDown() {
            instance?.volumeDown()
        }

        fun duck() {
            instance?.duck()
        }

        fun unduck() {
            instance?.unduck()
        }

        fun setDefaultPlayer(player: String) {
            // No-op / compat
        }
    }

    init {
        instance = this
    }
    private val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())
    private var lastBookmark: Pair<String, Long>? = null

    private val _state = MutableStateFlow(MusicState.IDLE)
    val state = _state.asStateFlow()

    private val _volume = MutableStateFlow(0.7f)
    val volume = _volume.asStateFlow()
    private var baseVolume = 0.7f
    private var isDucked = false
    private var transitionJob: Job? = null

    init {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

        volume.onEach { vol ->
            val targetLevel = (vol * maxVolume).toInt()
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetLevel, 0)
        }.launchIn(scope)

        scope.launch {
            LogPoseLogger.d("MusicManager: Vinculando Spotify Remote...")
            val success = SpotifyRemoteManager.connect(context)
            if (success) LogPoseLogger.i("MusicManager: Spotify listo.")
        }
    }

    fun play(query: String = "") {
        _state.value = MusicState.MUSIC_PLAYING
        if (!SpotifyRemoteManager.isConnected()) {
            scope.launch {
                if (SpotifyRemoteManager.connect(context)) executePlay(query)
                else executePlay(query)
            }
            return
        }
        executePlay(query)
    }

    private fun executePlay(query: String) {
        if (query.isBlank()) {
            val bookmark = lastBookmark
            if (bookmark != null) SpotifyRemoteManager.seekAndPlay(bookmark.first, bookmark.second)
            else if (!SpotifyRemoteManager.isConnected()) SpotifyRemoteManager.searchAndPlay("")
            else SpotifyRemoteManager.resume()
        } else {
            if (query.startsWith("spotify:")) {
                if (query.contains(":search:")) SpotifyRemoteManager.searchAndPlay(query)
                else SpotifyRemoteManager.play(query)
            } else {
                SpotifyRemoteManager.searchAndPlay(query)
            }
        }
    }

    fun pause() {
        _state.value = MusicState.MUSIC_PAUSED
        if (!SpotifyRemoteManager.isConnected()) {
            scope.launch { SpotifyRemoteManager.connect(context); executePause() }
            return
        }
        executePause()
    }

    private fun executePause() {
        lastBookmark = SpotifyRemoteManager.getCurrentBookmark()
        SpotifyRemoteManager.pause()
    }

    fun next() {
        if (!SpotifyRemoteManager.isConnected()) {
            scope.launch { SpotifyRemoteManager.connect(context); SpotifyRemoteManager.next() }
            return
        }
        SpotifyRemoteManager.next()
    }

    fun previous() {
        if (!SpotifyRemoteManager.isConnected()) {
            scope.launch { SpotifyRemoteManager.connect(context); SpotifyRemoteManager.previous() }
            return
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

    fun duck() {
        if (isDucked) return
        isDucked = true
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
}
