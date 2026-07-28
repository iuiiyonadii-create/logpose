package com.uriel.logpose.thamis.safety.metrics

/**
 * KPIs de seguridad y reducción de distracciones.
 */
object SafetyMetrics {
    var avoidedInterruptions = 0
    var generatedAlerts = 0
    var averageRiskScore = 0f
    private var riskSum = 0f
    private var evaluationsCount = 0

    fun recordEvaluation(riskLevel: Int) {
        evaluationsCount++
        riskSum += riskLevel
        averageRiskScore = riskSum / evaluationsCount
    }

    fun recordAvoidedInterruption() {
        avoidedInterruptions++
    }
}
