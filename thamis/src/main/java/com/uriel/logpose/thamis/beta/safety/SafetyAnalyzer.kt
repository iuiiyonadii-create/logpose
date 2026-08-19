package com.uriel.logpose.thamis.beta.safety

import com.uriel.logpose.thamis.beta.model.DrivingSession
import com.uriel.logpose.thamis.beta.model.SafetyReport

/**
 * Analiza una sesión de conducción para identificar riesgos o distracciones.
 */
object SafetyAnalyzer {

    fun analyze(session: DrivingSession): SafetyReport {
        val interruptions = session.events.count { it.contains("INTERRUPT") }
        val errors = session.events.count { it.contains("ERROR") }
        
        val safetyScore = if (interruptions > 10) 0.5f else 1.0f

        return SafetyReport(
            sessionId = session.id,
            interruptionCount = interruptions,
            riskEventsDetected = errors,
            distractionWarnings = if (safetyScore < 0.7f) listOf("Demasiadas interrupciones auditivas") else emptyList(),
            safetyScore = safetyScore
        )
    }
}
