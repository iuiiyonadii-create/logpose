package com.uriel.logpose.thamis.performance.report

import com.uriel.logpose.thamis.performance.latency.LatencyAnalyzer
import com.uriel.logpose.thamis.performance.model.PerformanceReport
import com.uriel.logpose.thamis.performance.telemetry.TelemetryEngine
import com.uriel.logpose.thamis.performance.model.PerformanceEvent

/**
 * Genera informes consolidados de rendimiento.
 */
object PerformanceReportGenerator {

    fun generate(): PerformanceReport {
        val modules = listOf("Navigation", "Multimedia", "Planning", "Communication", "Dialog")
        val issues = mutableListOf<String>()
        val recommendations = mutableListOf<String>()

        modules.forEach { module ->
            val avg = LatencyAnalyzer.getAverageLatency(module)
            if (avg > 300.0) {
                issues.add("Módulo $module presenta latencia elevada: ${"%.2f".format(avg)}ms")
                recommendations.add("Optimizar pipeline de $module")
            }
        }

        val errors = TelemetryEngine.getHistory().filterIsInstance<PerformanceEvent.ModuleFailed>()
        if (errors.isNotEmpty()) {
            issues.add("Se detectaron ${errors.size} fallos en el historial")
        }

        return PerformanceReport(
            summary = "Performance Report v1.0 - ${if (issues.isEmpty()) "STATUS OK" else "STATUS DEGRADED"}",
            modulesEvaluated = modules,
            issuesFound = issues,
            recommendations = recommendations
        )
    }
}
