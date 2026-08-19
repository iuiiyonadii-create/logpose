package com.uriel.logpose.thamis.voice.confidence

import com.uriel.logpose.thamis.voice.model.ConfidenceLevel

/**
 * Evalúa el nivel de confianza de una interpretación vocal.
 */
object ConfidenceEvaluator {

    fun evaluate(score: Float): ConfidenceLevel {
        return when {
            score >= 0.85f -> ConfidenceLevel.HIGH_CONFIDENCE
            score >= 0.60f -> ConfidenceLevel.MEDIUM_CONFIDENCE
            else -> ConfidenceLevel.LOW_CONFIDENCE
        }
    }
}
