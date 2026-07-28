package com.uriel.logpose.thamis.integration.orchestrator

import com.uriel.logpose.thamis.integration.pipeline.IntegrationPipeline
import com.uriel.logpose.thamis.integration.model.PipelineResult

/**
 * Orquestador central de la integración cognitiva.
 */
object IntegrationOrchestrator {

    fun orchestrate(goal: String): PipelineResult {
        val pipeline = IntegrationPipeline()
        
        // El orden inmutable de la orquestación v1.0
        pipeline.addStage("WorldSnapshot")
        pipeline.addStage("Dialog")
        pipeline.addStage("Intent")
        pipeline.addStage("Planning")
        pipeline.addStage("Safety")
        pipeline.addStage("Authority")
        pipeline.addStage("Actuator")

        return pipeline.execute()
    }
}
