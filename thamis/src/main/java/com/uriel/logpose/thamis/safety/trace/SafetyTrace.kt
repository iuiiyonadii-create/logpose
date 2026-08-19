package com.uriel.logpose.thamis.safety.trace

import com.uriel.logpose.thamis.safety.model.RiskLevel
import com.uriel.logpose.thamis.safety.model.SafetyAction

/**
 * Registro forense de decisiones de seguridad.
 */
data class SafetyTrace(
    val contextDescription: String,
    val riskLevel: RiskLevel,
    val decision: SafetyAction,
    val reason: String,
    val timestamp: Long = System.currentTimeMillis()
)

object SafetyAudit {
    private val logs = mutableListOf<SafetyTrace>()

    fun record(trace: SafetyTrace) {
        logs.add(trace)
        if (logs.size > 200) logs.removeAt(0)
    }

    fun getLogs(): List<SafetyTrace> = logs.toList()
}
