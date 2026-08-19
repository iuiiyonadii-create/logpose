package com.uriel.logpose.thamis.proactive.metrics

/**
 * KPIs del motor proactivo.
 */
object ProactiveMetrics {
    var totalSuggestions = 0
    var acceptedSuggestions = 0
    var ignoredSuggestions = 0
    var avoidedInterruptions = 0

    fun recordSuggestion(isAccepted: Boolean) {
        totalSuggestions++
        if (isAccepted) acceptedSuggestions++ else ignoredSuggestions++
    }

    fun recordAvoidance() {
        avoidedInterruptions++
    }
}
