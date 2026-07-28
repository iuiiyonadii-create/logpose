package com.uriel.logpose.features.voice

import java.util.concurrent.atomic.AtomicBoolean

/**
 * PlaybackAwareMicGate: Controla el estado del "oído" del sistema.
 * Bloquea la escucha si hay música sonando o si el sistema está hablando.
 */
class PlaybackAwareMicGate {

    private val mutedByTts = AtomicBoolean(false)
    private val mutedByPlayback = AtomicBoolean(false)

    fun onTtsStarted() { mutedByTts.set(true) }
    fun onTtsEnded() { mutedByTts.set(false) }

    fun onPlaybackStarted() { mutedByPlayback.set(true) }
    fun onPlaybackStopped() { mutedByPlayback.set(false) }

    fun isGateOpen(): Boolean = !mutedByTts.get() && !mutedByPlayback.get()
}
