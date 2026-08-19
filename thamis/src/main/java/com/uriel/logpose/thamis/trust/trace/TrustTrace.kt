package com.uriel.logpose.thamis.trust.trace

/**
 * Registro de auditoría de interacciones de confianza.
 */
data class TrustTrace(
    val event: String,
    val decision: String,
    val explanation: String,
    val userSatisfied: Boolean?,
    val timestamp: Long = System.currentTimeMillis()
)

object TrustAudit {
    private val logs = mutableListOf<TrustTrace>()

    fun record(trace: TrustTrace) {
        logs.add(trace)
        if (logs.size > 200) logs.removeAt(0)
    }

    fun getLogs(): List<TrustTrace> = logs.toList()
}
