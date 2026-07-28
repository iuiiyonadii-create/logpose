package com.uriel.logpose.thamis.monitoring.anomaly

import com.uriel.logpose.thamis.monitoring.model.*
import com.uriel.logpose.thamis.monitoring.telemetry.TelemetryCollector

/**
 * Detecta patrones de ejecución fuera de los parámetros normales.
 */
object AnomalyDetector {

    fun detectAnomalies(): List<Anomaly> {
        val anomalies = mutableListOf<Anomaly>()
        
        // Simular detección por latencia elevada
        val domains = listOf("Planning", "WorldModel")
        domains.forEach { domain ->
            val history = TelemetryCollector.getPerformanceHistory(domain)
            if (history.any { it.executionTimeMs > 1000 }) {
                anomalies.add(Anomaly(
                    module = domain,
                    type = AnomalyType.HIGH_LATENCY,
                    severity = Severity.HIGH,
                    description = "Latencia crítica detectada en $domain (>1000ms)"
                ))
            }
        }

        return anomalies
    }
}
