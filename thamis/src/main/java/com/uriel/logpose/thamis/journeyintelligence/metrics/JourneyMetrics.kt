package com.uriel.logpose.thamis.journeyintelligence.metrics

/**
 * KPIs de inteligencia de viaje.
 */
object JourneyMetrics {
    var totalJourneys = 0
    var averageDurationMs = 0L
    var patternsDetected = 0
    var insightsGenerated = 0

    fun recordJourney(duration: Long, patternFound: Boolean) {
        totalJourneys++
        averageDurationMs = (averageDurationMs + duration) / 2
        if (patternFound) patternsDetected++
    }

    fun recordInsight() { insightsGenerated++ }
}
