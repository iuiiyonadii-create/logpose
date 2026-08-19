package com.uriel.logpose.thamis.performance.trace

/**
 * Registro auditable de eventos de rendimiento.
 */
data class PerformanceTrace(
    val timestamp: Long = System.currentTimeMillis(),
    val module: String,
    val event: String,
    val durationMs: Long,
    val result: String,
    val context: String,
    val error: String? = null
)

object PerformanceAudit {
    private val traces = mutableListOf<PerformanceTrace>()

    fun record(trace: PerformanceTrace) {
        traces.add(trace)
        if (traces.size > 500) traces.removeAt(0)
    }

    fun getLogs(): List<PerformanceTrace> = traces.toList()
}
