package com.uriel.logpose.core.services

import android.media.AudioTrack
import android.os.Handler
import android.os.HandlerThread
import android.util.Log

/**
 * Controla el ducking del AudioTrack de keep-alive SIN tocar su estado de reproducción.
 */
class AudioDuckController(private val keepAliveTrack: AudioTrack) {

    companion object {
        private const val TAG = "AudioDuckController"
        private const val PRE_COMMAND_GATE_MS = 200L
        private const val POST_COMMAND_RESTORE_MS = 300L
    }

    private val handlerThread = HandlerThread("AudioDuckThread").apply { start() }
    private val handler = Handler(handlerThread.looper)

    fun duckAndDispatch(command: () -> Unit) {
        setVolumeSafe(0f)

        handler.postDelayed({
            command()
            handler.postDelayed({ restoreVolume() }, POST_COMMAND_RESTORE_MS)
        }, PRE_COMMAND_GATE_MS)
    }

    private fun restoreVolume() {
        setVolumeSafe(1f)
    }

    private fun setVolumeSafe(volume: Float) {
        try {
            keepAliveTrack.setVolume(volume)
        } catch (e: IllegalStateException) {
            Log.w(TAG, "Track de keep-alive no inicializado.")
        }
    }

    fun release() {
        handlerThread.quitSafely()
    }
}
