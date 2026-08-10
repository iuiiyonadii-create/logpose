package com.uriel.logpose.audio.perception

import android.media.audiofx.AutomaticGainControl
import android.media.audiofx.NoiseSuppressor
import com.uriel.logpose.core.compat.core.LogPoseLogger

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
            LogPoseLogger.i(TAG, "Hardware Noise Suppressor ACTIVADO.")
        } else {
            LogPoseLogger.w(TAG, "Hardware Noise Suppressor NO disponible.")
        }

        if (AutomaticGainControl.isAvailable()) {
            val agc = AutomaticGainControl.create(audioSessionId)
            agc?.enabled = true
            LogPoseLogger.i(TAG, "Hardware Automatic Gain Control ACTIVADO.")
        } else {
            LogPoseLogger.w(TAG, "Hardware AGC NO disponible.")
        }
    }
}
