package com.uriel.logpose.thamis.trust.explanation

import com.uriel.logpose.thamis.trust.model.ReasoningTrace

/**
 * Gestor de trazas de razonamiento para auditoría interna y transparencia.
 */
object ReasoningTraceManager {
    private val traces = mutableListOf<ReasoningTrace>()

    fun record(event: String, rules: List<String>, decision: String) {
        traces.add(ReasoningTrace(
            event = event,
            contextSnapshotId = "CURRENT_SNAP",
            rulesApplied = rules,
            finalDecision = decision
        ))
        if (traces.size > 100) traces.removeAt(0)
    }

    fun getFullHistory(): List<ReasoningTrace> = traces.toList()
}
