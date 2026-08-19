package com.uriel.logpose.thamis.journey.audit

import com.uriel.logpose.thamis.journey.model.JourneyTransition
import com.uriel.logpose.thamis.journey.model.JourneyEvidence

/**
 * Registro granular de cada cambio de estado en el Journey Engine.
 */
data class JourneyTrace(
    val transition: JourneyTransition,
    val evidences: List<JourneyEvidence>,
    val speed: Float,
    val confidence: Float,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Auditoría central del dominio Journey.
 */
object JourneyAudit {
    private val traceLog = mutableListOf<JourneyTrace>()

    fun record(trace: JourneyTrace) {
        traceLog.add(trace)
        if (traceLog.size > 100) traceLog.removeAt(0)
    }

    fun getTraces(): List<JourneyTrace> = traceLog.toList()
}
