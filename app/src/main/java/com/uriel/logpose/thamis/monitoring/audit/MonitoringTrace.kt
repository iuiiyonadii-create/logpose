package com.uriel.logpose.thamis.monitoring.audit

import com.uriel.logpose.thamis.monitoring.model.HealthState

/**
 * Registro auditable de la salud cerebral.
 */
data class MonitoringTrace(
    val module: String,
    val event: String,
    val state: HealthState,
    val score: Int,
    val latencyMs: Long,
    val diagnostic: String,
    val timestamp: Long = System.currentTimeMillis()
)

object MonitoringAudit {
    private val logs = mutableListOf<MonitoringTrace>()

    fun record(trace: MonitoringTrace) {
        logs.add(trace)
        if (logs.size > 200) logs.removeAt(0)
    }

    fun getLogs(): List<MonitoringTrace> = logs.toList()
}
