package com.uriel.logpose.thamis.performance.latency

import com.uriel.logpose.thamis.performance.model.PerformanceEvent
import com.uriel.logpose.thamis.performance.telemetry.TelemetryEngine

/**
 * Analiza tiempos de respuesta y detecta cuellos de botella.
 */
object LatencyAnalyzer {

    fun getAverageLatency(module: String): Double {
        val history = TelemetryEngine.getHistory()
            .filterIsInstance<PerformanceEvent.ModuleFinished>()
            .filter { it.module == module }
        
        if (history.isEmpty()) return 0.0
        return history.map { it.durationMs }.average()
    }

    fun getMaxLatency(module: String): Long {
        return TelemetryEngine.getHistory()
            .filterIsInstance<PerformanceEvent.ModuleFinished>()
            .filter { it.module == module }
            .maxOfOrNull { it.durationMs } ?: 0L
    }
}
