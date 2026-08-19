package com.uriel.logpose.thamis.integration

import com.uriel.logpose.thamis.integration.orchestrator.IntegrationOrchestrator
import com.uriel.logpose.thamis.integration.model.PipelineResult
import com.uriel.logpose.thamis.integration.dispatcher.EventBus
import com.uriel.logpose.thamis.integration.model.EventType
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * El nuevo punto de entrada unificado para el cerebro THAMIS.
 */
object CognitiveIntegrationEngine {

    fun processRequest(goal: String) {
        LogPoseLogger.i("[THAMIS_INTEGRATION] Starting pipeline for goal: $goal")
        
        val result = IntegrationOrchestrator.orchestrate(goal)

        if (result == PipelineResult.SUCCESS) {
            EventBus.publish(EventType.PlanCreated, "CognitiveIntegrationEngine", goal)
            LogPoseLogger.i("[THAMIS_INTEGRATION] Pipeline finished successfully.")
        } else {
            LogPoseLogger.w("[THAMIS_INTEGRATION] Pipeline failed or rejected: $result")
        }
    }
}
