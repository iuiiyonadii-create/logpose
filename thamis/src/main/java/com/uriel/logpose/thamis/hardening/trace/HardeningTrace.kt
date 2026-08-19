package com.uriel.logpose.thamis.hardening.trace

/**
 * Registro auditable de eventos de endurecimiento y resolución de fallos.
 */
data class HardeningTrace(
    val event: String,
    val module: String,
    val issue: String,
    val resolution: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)

object HardeningAuditLog {
    private val logs = mutableListOf<HardeningTrace>()

    fun record(trace: HardeningTrace) {
        logs.add(trace)
        if (logs.size > 200) logs.removeAt(0)
    }

    fun getLogs(): List<HardeningTrace> = logs.toList()
}
