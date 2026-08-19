package com.uriel.logpose.thamis.journey.model

/**
 * Evidencias ponderadas para determinar el estado del viaje.
 */
data class JourneyEvidence(
    val type: Type,
    val weight: Float,
    val value: Any,
    val confidence: Float,
    val timestamp: Long = System.currentTimeMillis()
) {
    enum class Type {
        GPS, BLUETOOTH, HELMET, SPEED, MOVEMENT, IDLE_TIME, NAVIGATION, CALL, MUSIC, BATTERY, CHARGING
    }
}

/**
 * Representa una transición de estado auditable.
 */
data class JourneyTransition(
    val from: JourneyState,
    val to: JourneyState,
    val trigger: JourneyEvent,
    val timestamp: Long = System.currentTimeMillis(),
    val reason: String
)
