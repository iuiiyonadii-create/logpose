package com.uriel.logpose.thamis.intent

/**
 * Resultado de la detección de intención.
 */
data class DetectionResult(
    val intent: Intent,
    val score: Float
)