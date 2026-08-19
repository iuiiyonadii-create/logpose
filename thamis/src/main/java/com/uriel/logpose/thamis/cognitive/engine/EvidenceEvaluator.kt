package com.uriel.logpose.thamis.cognitive.engine

import com.uriel.logpose.thamis.cognitive.model.Evidence
import com.uriel.logpose.thamis.cognitive.model.Hypothesis
import com.uriel.logpose.thamis.cognitive.model.WorldState

/**
 * Interfaz para componentes que analizan aspectos específicos de la realidad
 * para emitir evidencias a favor o en contra de una hipótesis.
 */
interface EvidenceEvaluator {
    fun evaluate(hypothesis: Hypothesis, worldState: WorldState): List<Evidence>
}

/**
 * Evalúa el estado del sistema (música, llamadas, bluetooth).
 */
class ContextEvidenceEvaluator : EvidenceEvaluator {
    override fun evaluate(hypothesis: Hypothesis, worldState: WorldState): List<Evidence> {
        val evidences = mutableListOf<Evidence>()
        
        // Si el objetivo es multimedia y Spotify está sonando, boost de confianza
        if (hypothesis.candidateGoal.category == com.uriel.logpose.thamis.cognitive.model.Goal.Category.MULTIMEDIA && 
            worldState.system.isMusicPlaying) {
            evidences.add(Evidence(
                type = Evidence.Type.POSITIVE,
                source = Evidence.Source.CONTEXT_SYSTEM,
                impact = 0.2f,
                description = "Spotify activo favorece intención multimedia",
                expirationMs = 5000L
            ))
        }
        
        return evidences
    }
}

/**
 * Evalúa el riesgo situacional (velocidad, movimiento).
 */
class RiskEvidenceEvaluator : EvidenceEvaluator {
    override fun evaluate(hypothesis: Hypothesis, worldState: WorldState): List<Evidence> {
        val evidences = mutableListOf<Evidence>()
        
        // Si la moto está en movimiento rápido, penalizamos levemente la confianza para forzar seguridad
        if (worldState.driving.isMoving && worldState.driving.speedKmh > 60) {
            evidences.add(Evidence(
                type = Evidence.Type.NEGATIVE,
                source = Evidence.Source.CONTEXT_WORLD,
                impact = -0.1f,
                description = "Velocidad alta aumenta riesgo de falso positivo",
                expirationMs = 2000L
            ))
        }
        
        return evidences
    }
}
