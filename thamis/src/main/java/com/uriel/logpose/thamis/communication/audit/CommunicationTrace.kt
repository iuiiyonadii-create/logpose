package com.uriel.logpose.thamis.communication.audit

import com.uriel.logpose.thamis.communication.model.CommunicationDecision
import com.uriel.logpose.thamis.communication.model.CommunicationGoal

/**
 * Registro forense de una decisión de comunicación.
 */
data class CommunicationTrace(
    val input: String,
    val goal: CommunicationGoal,
    val decision: CommunicationDecision,
    val latencyMs: Long,
    val timestamp: Long = System.currentTimeMillis()
)

object CommunicationAudit {
    private val traceLog = mutableListOf<CommunicationTrace>()

    fun record(trace: CommunicationTrace) {
        traceLog.add(trace)
        if (traceLog.size > 200) traceLog.removeAt(0)
    }

    fun getTraces(): List<CommunicationTrace> = traceLog.toList()
}
