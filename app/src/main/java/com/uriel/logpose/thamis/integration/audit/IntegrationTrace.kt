package com.uriel.logpose.thamis.integration.audit

import com.uriel.logpose.thamis.integration.model.PipelineResult

/**
 * Registro granular de la ejecución del pipeline de integración.
 */
data class IntegrationTrace(
    val goal: String,
    val totalTimeMs: Long,
    val stages: List<String>,
    val result: PipelineResult,
    val timestamp: Long = System.currentTimeMillis()
)

object IntegrationAudit {
    private val logs = mutableListOf<IntegrationTrace>()

    fun record(trace: IntegrationTrace) {
        logs.add(trace)
        if (logs.size > 200) logs.removeAt(0)
    }

    fun getLogs(): List<IntegrationTrace> = logs.toList()
}
