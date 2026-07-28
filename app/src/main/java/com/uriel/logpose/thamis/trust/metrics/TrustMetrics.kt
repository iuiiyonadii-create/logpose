package com.uriel.logpose.thamis.trust.metrics

/**
 * KPIs de transparencia y percepción de confianza.
 */
object TrustMetrics {
    var explanationsGenerated = 0
    var userQueriesCount = 0
    var averageDecisionConfidence = 0.95f
    var auditedDecisionsCount = 0

    fun recordExplanation() { explanationsGenerated++ }
    fun recordUserQuery() { userQueriesCount++ }
}
