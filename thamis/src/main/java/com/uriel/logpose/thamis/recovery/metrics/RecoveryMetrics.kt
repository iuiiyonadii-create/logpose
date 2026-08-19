package com.uriel.logpose.thamis.recovery.metrics

/**
 * Recolector de KPIs de resiliencia.
 */
object RecoveryMetrics {
    var totalRecoveryAttempts = 0
    var approvedCount = 0
    var rejectedCount = 0
    var averageRisk = 0f
    private var riskSum = 0f

    fun recordAttempt(isApproved: Boolean, risk: Float) {
        totalRecoveryAttempts++
        if (isApproved) approvedCount++ else rejectedCount++
        
        riskSum += risk
        averageRisk = riskSum / totalRecoveryAttempts
    }
}
