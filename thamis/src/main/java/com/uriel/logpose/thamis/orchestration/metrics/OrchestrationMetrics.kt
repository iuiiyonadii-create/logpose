package com.uriel.logpose.thamis.orchestration.metrics

import com.uriel.logpose.thamis.orchestration.model.OrchestrationDecision

/**
 * Recolector de KPIs del orquestador global.
 */
object OrchestrationMetrics {
    var totalRequests = 0
    var executions = 0
    var cancellations = 0
    var averageWaitTimeMs = 0L

    fun record(decision: OrchestrationDecision) {
        totalRequests++
        when (decision) {
            OrchestrationDecision.EXECUTE_NOW -> executions++
            OrchestrationDecision.CANCEL -> cancellations++
            else -> {}
        }
    }
}
