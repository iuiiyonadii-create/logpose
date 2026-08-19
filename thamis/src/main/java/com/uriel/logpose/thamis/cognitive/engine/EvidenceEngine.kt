package com.uriel.logpose.thamis.cognitive.engine

import com.uriel.logpose.thamis.cognitive.model.Evidence
import com.uriel.logpose.thamis.cognitive.model.Hypothesis
import com.uriel.logpose.thamis.cognitive.model.WorldState

/**
 * Motor central de Evidencias de THAMIS v3.0.
 * Su misión es enriquecer hipótesis con justificaciones matemáticas y contextuales.
 */
object EvidenceEngine {

    private val evaluators = listOf(
        GrammarEvidenceEvaluator(),
        PhoneticEvidenceEvaluator(),
        ContextEvidenceEvaluator(),
        RiskEvidenceEvaluator(),
        NavigationEvidenceEvaluator()
    )

    /**
     * Analiza una hipótesis contra la realidad y calcula su confianza final.
     */
    fun process(hypothesis: Hypothesis, worldState: WorldState): Hypothesis {
        val newEvidences = mutableListOf<Evidence>()
        
        // 1. Recolectar evidencias de todos los evaluadores
        for (evaluator in evaluators) {
            newEvidences.addAll(evaluator.evaluate(hypothesis, worldState))
        }

        // 2. Combinar con las evidencias internas que ya traía la hipótesis
        val totalEvidences = hypothesis.evidences + newEvidences

        // 3. Recalcular confianza
        val finalConfidence = ConfidenceCalculator.calculate(
            hypothesis.rawConfidence, 
            totalEvidences
        )

        // 4. Retornar hipótesis enriquecida
        return hypothesis.copy(
            evidences = totalEvidences,
            rawConfidence = finalConfidence
        )
    }
}
