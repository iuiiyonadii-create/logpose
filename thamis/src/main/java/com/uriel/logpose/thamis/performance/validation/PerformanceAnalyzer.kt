package com.uriel.logpose.thamis.performance.validation

import com.uriel.logpose.thamis.performance.telemetry.TelemetryEngine
import com.uriel.logpose.thamis.performance.model.PerformanceEvent

/**
 * Validador de consistencia y analizador de métricas.
 */
object PerformanceAnalyzer {

    fun checkInconsistencies(): List<String> {
        val issues = mutableListOf<String>()
        val history = TelemetryEngine.getHistory()

        // Detectar si hay muchos ModuleStarted sin ModuleFinished
        val startedCount = history.count { it is PerformanceEvent.ModuleStarted }
        val finishedCount = history.count { it is PerformanceEvent.ModuleFinished }
        
        if (startedCount > finishedCount + 10) {
            issues.add("Detección de procesos colgados: $startedCount iniciados vs $finishedCount finalizados")
        }

        return issues
    }
}
