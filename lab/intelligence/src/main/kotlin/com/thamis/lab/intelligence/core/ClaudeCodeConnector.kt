package com.thamis.lab.intelligence.core

import com.thamis.lab.core.common.ai.ThamisHttpConnector
import com.thamis.lab.core.common.logging.LabLogger
import com.thamis.lab.core.common.result.LabResult
import com.thamis.lab.core.common.ai.AgenticResponse

/**
 * ClaudeCodeConnector: Specialization of the Thamis HTTP connector for Intelligence Hub.
 */
public class ClaudeCodeConnector : ThamisHttpConnector() {
    override val providerName: String = "THAMIS Neural Brain (Flask/Intelligence)"
    
    private val telemetryBatch = mutableListOf<String>()
    private val BATCH_SIZE = 5

    override fun analyzeTask(prompt: String): LabResult<AgenticResponse> {
        if (prompt.contains("USAGE_STATS")) {
            telemetryBatch.add(prompt)
            if (telemetryBatch.size < BATCH_SIZE) {
                return LabResult.Success(AgenticResponse("Batching..."))
            }
            val batchedPrompt = "BATCHED_TELEMETRY: \n" + telemetryBatch.joinToString("\n")
            telemetryBatch.clear()
            return super.analyzeTask(batchedPrompt)
        }
        
        val result = super.analyzeTask(prompt)
        
        // Log thinking for transparency
        if (result.isSuccess) {
            val thinking = result.getOrNull()?.thinking ?: ""
            if (thinking.isNotBlank() && !thinking.startsWith("Analyzing project")) {
                LabLogger.info("ClaudeConnector", "🧠 Agente pensando: $thinking")
            }
        }
        
        return result
    }
}
