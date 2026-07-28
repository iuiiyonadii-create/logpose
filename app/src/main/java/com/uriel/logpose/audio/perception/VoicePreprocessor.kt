package com.uriel.logpose.audio.perception

import android.media.audiofx.AutomaticGainControl
import android.media.audiofx.NoiseSuppressor
import android.util.Log

/**
 * Pre-procesador de audio antes de la fase de reconocimiento (STT).
 * Intenta utilizar las APIs nativas de Android para limpieza de señal.
 */
object VoicePreprocessor {
    private const val TAG = "THAMIS_AUDIO_PRE"

    fun setupHardwareEffects(audioSessionId: Int) {
        if (audioSessionId == -1) return

        if (NoiseSuppressor.isAvailable()) {
            val ns = NoiseSuppressor.create(audioSessionId)
            ns?.enabled = true
            Log.i(TAG, "Hardware Noise Suppressor ACTIVADO.")
        } else {
            Log.w(TAG, "Hardware Noise Suppressor NO disponible.")
        }

        if (AutomaticGainControl.isAvailable()) {
            val agc = AutomaticGainControl.create(audioSessionId)
            agc?.enabled = true
            Log.i(TAG, "Hardware Automatic Gain Control ACTIVADO.")
        } else {
            Log.w(TAG, "Hardware AGC NO disponible.")
        }
    }
}
