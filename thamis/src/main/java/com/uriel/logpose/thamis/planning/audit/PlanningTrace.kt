package com.uriel.logpose.thamis.planning.audit

import com.uriel.logpose.thamis.planning.model.*

/**
 * Registro detallado de la planificación y auditoría.
 */
data class PlanningTrace(
    val snapshotId: String,
    val goal: String,
    val plan: ExecutionPlan,
    val strategy: PlanningStrategy,
    val priority: PlanningPriority,
    val decision: PlanningDecision,
    val reason: String,
    val timestamp: Long = System.currentTimeMillis()
)

object PlanningAudit {
    private val logs = mutableListOf<PlanningTrace>()

    fun record(trace: PlanningTrace) {
        logs.add(trace)
        if (logs.size > 200) logs.removeAt(0)
    }

    fun getLogs(): List<PlanningTrace> = logs.toList()
}
