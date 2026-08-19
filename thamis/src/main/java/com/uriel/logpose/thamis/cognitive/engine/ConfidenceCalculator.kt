package com.uriel.logpose.thamis.cognitive.engine

import com.uriel.logpose.thamis.cognitive.model.Evidence

/**
 * Encargado de transformar una lista de evidencias en un puntaje de confianza (0.0 a 1.0).
 * Hardened v1.2: Utiliza suma ponderada y amortiguación para evitar oscilaciones por ruido.
 */
object ConfidenceCalculator {

    private const val DAMPENING_FACTOR = 0.9f // Reduce impacto de evidencias acumuladas

    fun calculate(baseScore: Float, evidences: List<Evidence>): Float {
        if (evidences.isEmpty()) return baseScore
        
        // 1. Filtrar solo evidencias válidas (No expiradas)
        val validEvidences = evidences.filter { it.isStillValid() }
        
        var positiveImpact = 0f
        var negativeImpact = 0f

        for (evidence in validEvidences) {
            if (evidence.impact > 0) {
                positiveImpact += evidence.impact
            } else {
                negativeImpact += evidence.impact // impact es negativo
            }
        }

        // 2. Aplicar Amortiguación (Dampening)
        // La idea es que muchas evidencias pequeñas no sumen linealmente hasta 1.0
        // sino que sigan una curva asintótica.
        val weightedPos = (1.0 - Math.exp(-positiveImpact.toDouble() * DAMPENING_FACTOR)).toFloat()
        val weightedNeg = (1.0 - Math.exp(negativeImpact.toDouble() * DAMPENING_FACTOR)).toFloat()

        // 3. Combinar con base
        val finalScore = baseScore + (weightedPos * 0.5f) + (weightedNeg * 0.5f)

        return finalScore.coerceIn(0.0f, 1.0f)
    }
}
