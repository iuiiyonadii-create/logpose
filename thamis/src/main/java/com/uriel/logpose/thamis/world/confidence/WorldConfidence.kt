package com.uriel.logpose.thamis.world.confidence

/**
 * Representa el nivel de confianza en un dato específico del modelo del mundo.
 */
data class WorldConfidence(
    val score: Float, // 0.0 to 1.0
    val timestamp: Long,
    val provider: String,
    val source: String, // GPS, Accelerometer, Bluetooth, etc.
    val validityMs: Long = 5000L
) {
    fun isExpired(): Boolean = System.currentTimeMillis() - timestamp > validityMs
}

data class WorldEvidence(
    val type: String,
    val value: Any,
    val confidence: WorldConfidence
)
