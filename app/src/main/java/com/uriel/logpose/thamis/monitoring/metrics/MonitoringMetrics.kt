package com.uriel.logpose.thamis.monitoring.metrics

/**
 * KPIs de auto-monitoreo.
 */
object MonitoringMetrics {
    var averageHealthScore = 0f
    var totalDiagnostics = 0
    var criticalAnomaliesCount = 0
    private var healthSum = 0f

    fun recordDiagnostic(score: Int, criticalAnomalies: Int) {
        totalDiagnostics++
        healthSum += score
        averageHealthScore = healthSum / totalDiagnostics
        criticalAnomaliesCount += criticalAnomalies
    }
}
