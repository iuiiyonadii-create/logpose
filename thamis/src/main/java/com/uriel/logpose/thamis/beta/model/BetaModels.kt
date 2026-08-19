package com.uriel.logpose.thamis.beta.model

import java.util.*

/**
 * Escenarios de conducción para pruebas beta.
 */
enum class DrivingScenario {
    CITY,
    HIGHWAY,
    DELIVERY,
    TRAFFIC,
    NIGHT,
    RAIN
}

/**
 * Sesión de prueba de conducción real.
 */
data class DrivingSession(
    val id: String = UUID.randomUUID().toString(),
    val startTime: Long = System.currentTimeMillis(),
    var endTime: Long? = null,
    val scenario: DrivingScenario,
    val events: MutableList<String> = mutableListOf(),
    var resultSummary: String? = null
)

/**
 * Retroalimentación directa del usuario beta.
 */
data class UserFeedback(
    val sessionId: String,
    val helpfulnessRating: Int, // 1 to 5
    val annoyanceRating: Int, // 1 to 5
    val confidenceRating: Int, // 1 to 5
    val qualitativeFeedback: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Informe de seguridad derivado de una sesión.
 */
data class SafetyReport(
    val sessionId: String,
    val interruptionCount: Int,
    val riskEventsDetected: Int,
    val distractionWarnings: List<String>,
    val safetyScore: Float // 0.0 to 1.0
)
