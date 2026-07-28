package com.uriel.logpose.thamis.journey.validation

import com.uriel.logpose.thamis.journey.model.JourneySession

/**
 * Reporte de validación de consistencia de un viaje.
 */
data class JourneyValidationReport(
    val sessionId: String,
    val accuracy: Float,
    val issues: List<String>
)

/**
 * Motor de validación encargado de auditar la calidad de los datos del viaje.
 */
object JourneyValidationEngine {
    
    fun validate(session: JourneySession): JourneyValidationReport {
        val issues = mutableListOf<String>()
        
        if (session.distanceMeters < 0) issues.add("Distancia negativa detectada")
        if (session.endTime != null && session.endTime!! < session.startTime) {
            issues.add("Fin de viaje anterior al inicio")
        }
        
        val accuracy = if (issues.isEmpty()) 1.0f else 0.7f
        
        return JourneyValidationReport(
            sessionId = session.id,
            accuracy = accuracy,
            issues = issues
        )
    }
}
