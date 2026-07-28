package com.uriel.logpose.thamis.planning.history

import com.uriel.logpose.thamis.planning.model.ExecutionPlan
import com.uriel.logpose.thamis.planning.model.PlanningResult

/**
 * Historial de planes ejecutados.
 */
object PlanningHistory {
    private val history = mutableListOf<ExecutionPlan>()
    private val results = mutableListOf<PlanningResult>()

    fun recordPlan(plan: ExecutionPlan) {
        history.add(plan)
        if (history.size > 500) history.removeAt(0)
    }

    fun recordResult(result: PlanningResult) {
        results.add(result)
        if (results.size > 500) results.removeAt(0)
    }

    fun getSuccessRate(): Float {
        if (results.isEmpty()) return 0f
        return results.count { it.success }.toFloat() / results.size
    }
}
