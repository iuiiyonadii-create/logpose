package com.uriel.logpose.thamis.hardware.trace

/**
 * Registro auditable de eventos físicos y de conexión.
 */
data class HardwareTrace(
    val device: String,
    val event: String,
    val latencyMs: Long,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)

object HardwareAudit {
    private val logs = mutableListOf<HardwareTrace>()

    fun record(trace: HardwareTrace) {
        logs.add(trace)
        if (logs.size > 200) logs.removeAt(0)
    }

    fun getLogs(): List<HardwareTrace> = logs.toList()
}
