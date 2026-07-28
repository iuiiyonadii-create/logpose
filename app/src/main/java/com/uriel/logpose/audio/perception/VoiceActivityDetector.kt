package com.uriel.logpose.audio.perception

/**
 * Detector de actividad de voz (VAD) simplificado.
 */
object VoiceActivityDetector {

    enum class State { SILENCE, VOICE_DETECTED, NOISE_ONLY }

    fun detect(rms: Double, noiseFloor: Double): State {
        return when {
            rms < noiseFloor * 1.2 -> State.SILENCE
            rms > noiseFloor * 2.5 -> State.VOICE_DETECTED
            else -> State.NOISE_ONLY
        }
    }
}
