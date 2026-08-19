package com.uriel.logpose.thamis.voiceexperience.silence

/**
 * Gestiona los periodos de silencio para evitar fatiga auditiva.
 */
object SilenceManager {
    private var lastInteractionTime = 0L

    fun canSpeakNow(minSilenceMs: Long = 3000L): Boolean {
        val now = System.currentTimeMillis()
        return (now - lastInteractionTime) > minSilenceMs
    }

    fun markInteraction() {
        lastInteractionTime = System.currentTimeMillis()
    }
}
