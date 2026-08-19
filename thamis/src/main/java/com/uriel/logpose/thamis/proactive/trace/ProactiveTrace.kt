package com.uriel.logpose.thamis.proactive.trace

import com.uriel.logpose.thamis.proactive.model.ProactiveDecision

/**
 * Registro de auditoría de decisiones proactivas.
 */
data class ProactiveTrace(
    val suggestionId: String,
    val decision: ProactiveDecision,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)

object ProactiveAudit {
    private val logs = mutableListOf<ProactiveTrace>()

    fun record(trace: ProactiveTrace) {
        logs.add(trace)
        if (logs.size > 200) logs.removeAt(0)
    }

    fun getLogs(): List<ProactiveTrace> = logs.toList()
}
