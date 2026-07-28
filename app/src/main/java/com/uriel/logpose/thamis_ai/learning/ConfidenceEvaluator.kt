package com.uriel.logpose.thamis_ai.learning

/**
 * Calculates the reliability of a learned pattern.
 */
class ConfidenceEvaluator {

    fun evaluate(frequency: Int): Float {
        return when {
            frequency >= 30 -> 1.0f
            frequency >= 10 -> 0.7f
            frequency >= 5 -> 0.4f
            else -> 0.1f
        }
    }
}
