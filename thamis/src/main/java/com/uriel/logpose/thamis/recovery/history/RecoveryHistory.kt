package com.uriel.logpose.thamis.recovery.history

import com.uriel.logpose.thamis.recovery.model.RecoveryDecision
import com.uriel.logpose.thamis.recovery.model.RecoveryPlan
import java.util.*

/**
 * Registro de historial de recuperaciones.
 */
object RecoveryHistory {
    private val history = mutableListOf<RecoveryHistoryEntry>()

    data class RecoveryHistoryEntry(
        val timestamp: Long = System.currentTimeMillis(),
        val planId: String,
        val issue: String,
        val strategy: String,
        val decision: RecoveryDecision,
        val result: String? = null
    )

    fun record(plan: RecoveryPlan, decision: RecoveryDecision) {
        history.add(RecoveryHistoryEntry(
            planId = plan.id,
            issue = plan.anomaly.type.name,
            strategy = plan.strategy.name,
            decision = decision
        ))
        
        if (history.size > 200) history.removeAt(0)
    }

    fun getLogs(): List<RecoveryHistoryEntry> = history.toList()
}
