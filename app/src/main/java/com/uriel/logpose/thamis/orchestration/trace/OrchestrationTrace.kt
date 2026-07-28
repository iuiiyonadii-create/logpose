package com.uriel.logpose.thamis.orchestration.trace

import com.uriel.logpose.thamis.orchestration.model.OrchestrationDecision
import com.uriel.logpose.thamis.orchestration.model.OrchestrationDomain

/**
 * Registro de auditoría de una decisión de orquestación.
 */
data class OrchestrationTrace(
    val actionId: String,
    val domain: OrchestrationDomain,
    val decision: OrchestrationDecision,
    val priority: Int,
    val reason: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Gestor de trazas de orquestación.
 */
object OrchestrationAudit {
    private val traceLog = mutableListOf<OrchestrationTrace>()

    fun record(trace: OrchestrationTrace) {
        traceLog.add(trace)
        if (traceLog.size > 200) traceLog.removeAt(0)
    }

    fun getTraces(): List<OrchestrationTrace> = traceLog.toList()
}
