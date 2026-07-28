package com.uriel.logpose.thamis.world.audit

/**
 * Registro auditable de un cambio en el modelo del mundo.
 */
data class WorldTrace(
    val snapshotId: String,
    val affectedDomain: String,
    val description: String,
    val latencyMs: Long,
    val timestamp: Long = System.currentTimeMillis()
)

object WorldAudit {
    private val logs = mutableListOf<WorldTrace>()

    fun record(trace: WorldTrace) {
        logs.add(trace)
        if (logs.size > 200) logs.removeAt(0)
    }

    fun getLogs(): List<WorldTrace> = logs.toList()
}
