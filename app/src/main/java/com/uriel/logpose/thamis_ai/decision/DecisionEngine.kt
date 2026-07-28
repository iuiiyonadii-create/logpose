package com.uriel.logpose.thamis_ai.decision

import com.uriel.logpose.domain.models.DrivingState

/**
 * Core coordinator for complex THAMIS decisions.
 */
class DecisionEngine {

    private val riskAnalyzer = RiskAnalyzer()
    private val priorityEvaluator = PriorityEvaluator()

    fun evaluate(context: DecisionContext, eventType: String): DecisionResult {
        val risk = riskAnalyzer.analyze(context.drivingState, eventType)
        val priority = priorityEvaluator.getLevel(eventType)

        return if (risk > 0.7f) {
            DecisionResult(DecisionAction.WAIT, 0.9f, "High riding risk detected")
        } else {
            DecisionResult(DecisionAction.EXECUTE, 1.0f, "Safe to proceed")
        }
    }
}
