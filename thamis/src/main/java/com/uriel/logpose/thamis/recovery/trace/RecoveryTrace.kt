package com.uriel.logpose.thamis.recovery.trace

import com.uriel.logpose.thamis.recovery.model.RecoveryPlan
import com.uriel.logpose.thamis.recovery.model.RecoveryDecision

/**
 * Registro granular de decisiones de resiliencia.
 */
data class RecoveryTrace(
    val snapshotId: String,
    val anomalyType: String,
    val planId: String,
    val strategy: String,
    val decision: RecoveryDecision,
    val timestamp: Long = System.currentTimeMillis()
)

object RecoveryAudit {
    private val logs = mutableListOf<RecoveryTrace>()

    fun record(trace: RecoveryTrace) {
        logs.add(trace)
        if (logs.size > 200) logs.removeAt(0)
    }

    fun getLogs(): List<RecoveryTrace> = logs.toList()
}
