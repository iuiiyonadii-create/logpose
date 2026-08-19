package com.uriel.logpose.thamis.journey.validation

import com.uriel.logpose.thamis.journey.model.JourneyEvidence

/**
 * Evaluador de evidencias ponderadas para determinar la confianza en el estado del viaje.
 */
object JourneyEvidenceEvaluator {
    
    private val weights = mapOf(
        JourneyEvidence.Type.GPS to 0.9f,
        JourneyEvidence.Type.BLUETOOTH to 0.7f,
        JourneyEvidence.Type.HELMET to 0.8f,
        JourneyEvidence.Type.SPEED to 1.0f,
        JourneyEvidence.Type.MOVEMENT to 0.6f,
        JourneyEvidence.Type.CHARGING to 0.4f
    )

    /**
     * Calcula un score de confianza global (0.0 a 1.0) basado en las evidencias actuales.
     */
    fun evaluate(evidences: List<JourneyEvidence>): Float {
        if (evidences.isEmpty()) return 0f
        
        var totalWeight = 0f
        var weightedSum = 0f
        
        evidences.forEach { evidence ->
            val weight = weights[evidence.type] ?: 0.5f
            weightedSum += evidence.confidence * weight
            totalWeight += weight
        }
        
        return if (totalWeight > 0) weightedSum / totalWeight else 0f
    }
}
