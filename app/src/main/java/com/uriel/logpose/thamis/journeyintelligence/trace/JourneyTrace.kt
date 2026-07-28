package com.uriel.logpose.thamis.journeyintelligence.trace

import com.uriel.logpose.thamis.journeyintelligence.model.JourneyContext
import com.uriel.logpose.thamis.journeyintelligence.model.JourneyInsight

/**
 * Registro de auditoría de inteligencia de viaje.
 */
data class JourneyTrace(
    val journeyId: String,
    val context: JourneyContext,
    val insights: List<JourneyInsight>,
    val timestamp: Long = System.currentTimeMillis()
)

object JourneyAudit {
    private val logs = mutableListOf<JourneyTrace>()

    fun record(trace: JourneyTrace) {
        logs.add(trace)
        if (logs.size > 200) logs.removeAt(0)
    }

    fun getLogs(): List<JourneyTrace> = logs.toList()
}
