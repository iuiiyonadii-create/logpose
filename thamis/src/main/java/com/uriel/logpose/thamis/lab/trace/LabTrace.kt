package com.uriel.logpose.thamis.lab.trace

/**
 * Registro de auditoría para cada paso de la simulación.
 */
data class LabTrace(
    val scenarioName: String,
    val condition: String,
    val affectedModule: String,
    val latencyMs: Long,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)

object LabAuditLog {
    private val logs = mutableListOf<LabTrace>()

    fun record(trace: LabTrace) {
        logs.add(trace)
        if (logs.size > 200) logs.removeAt(0)
    }

    fun getLogs(): List<LabTrace> = logs.toList()
}
