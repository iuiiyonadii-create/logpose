package com.uriel.logpose.thamis.improvement.metrics

/**
 * KPIs del sistema de evolución e infraestructura de mejora.
 */
object ImprovementMetrics {
    var totalProposals = 0
    var approvedCount = 0
    var implementedCount = 0
    var averageValidationTimeMs = 0L

    fun recordProposal() { totalProposals++ }
    fun recordApproval() { approvedCount++ }
    fun recordImplementation() { implementedCount++ }
}
