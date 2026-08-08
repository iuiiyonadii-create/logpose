package com.uriel.logpose.thamis.intent

import com.thamis.lab.core.contracts.intent.Intent

/**
 * Resultado de la detección de intención.
 */
data class DetectionResult(
    val intent: Intent,
    val score: Float,
    val entities: Map<String, String> = emptyMap()
)
