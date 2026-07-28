package com.uriel.logpose.thamis.validation

/**
 * Representa una sesión de prueba en el mundo real para THAMIS.
 */
data class ValidationSession(
    val sessionId: String,
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val durationMs: Long = 0,
    val totalSamples: Int = 0,
    val averageSpeedKmh: Int = 0,
    val averageNoiseLevel: Float = 0f,
    val engineVersion: String,
    val environment: Environment = Environment.CITY
) {
    enum class Environment {
        CITY,
        HIGHWAY,
        STATIONARY
    }
}
