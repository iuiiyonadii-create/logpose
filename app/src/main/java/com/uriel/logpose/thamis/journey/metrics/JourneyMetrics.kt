package com.uriel.logpose.thamis.journey.metrics

/**
 * Recolector de métricas de performance y precisión del Journey Engine.
 */
object JourneyMetrics {
    var falseStarts = 0
    var falseStops = 0
    var totalTransitions = 0
    var averageConfidence = 0f
    private var confidenceSum = 0f

    /**
     * Registra una transición exitosa.
     */
    fun recordTransition(confidence: Float) {
        totalTransitions++
        confidenceSum += confidence
        averageConfidence = confidenceSum / totalTransitions
    }

    fun recordFalseStart() { falseStarts++ }
    fun recordFalseStop() { falseStops++ }
}
