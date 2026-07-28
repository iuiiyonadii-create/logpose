package com.uriel.logpose.thamis.performance.model

import java.util.*

/**
 * Captura puntual de rendimiento de una operación.
 */
data class PerformanceSnapshot(
    val timestamp: Long = System.currentTimeMillis(),
    val module: String,
    val operation: String,
    val durationMs: Long,
    val result: String,
    val state: String
)

/**
 * Representa métricas de latencia acumuladas.
 */
data class LatencyMetric(
    val startTime: Long,
    val endTime: Long,
    val durationMs: Long,
    val averageMs: Double,
    val maxMs: Long,
    val minMs: Long
)

/**
 * Captura de uso de recursos (Memoria, CPU simulada).
 */
data class ResourceSnapshot(
    val timestamp: Long = System.currentTimeMillis(),
    val memoryUsageKb: Long,
    val activityLevel: Float, // 0.0 to 1.0
    val processFrequency: Float, // Hertz
    val loadFactor: Float // 0.0 to 1.0
)

/**
 * Informe consolidado de rendimiento.
 */
data class PerformanceReport(
    val summary: String,
    val modulesEvaluated: List<String>,
    val issuesFound: List<String>,
    val recommendations: List<String>,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Eventos de telemetría de rendimiento.
 */
sealed class PerformanceEvent {
    data class ModuleStarted(val module: String, val timestamp: Long = System.currentTimeMillis()) : PerformanceEvent()
    data class ModuleFinished(val module: String, val durationMs: Long, val timestamp: Long = System.currentTimeMillis()) : PerformanceEvent()
    data class ModuleFailed(val module: String, val error: String, val timestamp: Long = System.currentTimeMillis()) : PerformanceEvent()
    data class LatencyDetected(val module: String, val latencyMs: Long, val timestamp: Long = System.currentTimeMillis()) : PerformanceEvent()
    data class ResourceWarning(val resource: String, val description: String, val timestamp: Long = System.currentTimeMillis()) : PerformanceEvent()
    data class PerformanceDegraded(val module: String, val reason: String, val timestamp: Long = System.currentTimeMillis()) : PerformanceEvent()
}
