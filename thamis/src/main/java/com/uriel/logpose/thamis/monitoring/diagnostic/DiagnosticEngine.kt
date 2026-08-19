package com.uriel.logpose.thamis.monitoring.diagnostic

import com.uriel.logpose.thamis.monitoring.model.DiagnosticReport
import com.uriel.logpose.thamis.monitoring.health.HealthMonitor
import com.uriel.logpose.thamis.monitoring.anomaly.AnomalyDetector
import com.uriel.logpose.thamis.monitoring.recovery.RecoveryAdvisor

/**
 * Genera informes consolidados de la salud cerebral.
 */
object DiagnosticEngine {

    fun generateReport(): DiagnosticReport {
        val health = HealthMonitor.evaluateGlobalHealth()
        val anomalies = AnomalyDetector.detectAnomalies()
        val suggestions = RecoveryAdvisor.suggestRecoveries(anomalies)

        return DiagnosticReport(
            summary = "Salud Global: ${health.state} (Score: ${health.globalScore})",
            issuesFound = anomalies.map { it.description },
            recommendations = suggestions
        )
    }
}
