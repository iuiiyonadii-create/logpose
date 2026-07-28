package com.uriel.logpose.thamis.shadow

import com.uriel.logpose.thamis.cognitive.engine.EvidenceEngine
import com.uriel.logpose.thamis.cognitive.decision.DecisionEngine
import com.uriel.logpose.thamis.cognitive.decision.RiskEvaluator
import com.uriel.logpose.thamis.cognitive.model.*
import com.uriel.logpose.thamis.intent.Intent
import com.uriel.logpose.thamis.entity.EntityExtractor

/**
 * Orquestador del Modo Sombra v3.0.
 * Duplica el flujo de procesamiento hacia el cerebro cognitivo.
 */
object ShadowModeController {

    fun evaluate(text: String, legacyIntent: Intent, worldState: WorldState) {
        val startTime = System.currentTimeMillis()

        // 1. Replicar ciclo mental THAMIS v3.0
        
        // Simulación de Hipótesis (Mapeo temporal para Shadow Mode)
        val hypothesis = Hypothesis(
            candidateGoal = Goal(Goal.Category.MULTIMEDIA, 0.5f, emptyMap(), "play"),
            entities = EntityExtractor.extract(legacyIntent, text),
            evidences = emptyList(),
            rawConfidence = 0.5f
        )

        // Evaluar con EvidenceEngine
        val enrichedHypothesis = EvidenceEngine.process(hypothesis, worldState)
        
        // Evaluar Riesgo
        val risk = RiskEvaluator.evaluate(enrichedHypothesis.candidateGoal)
        
        // Crear Evaluación
        val evaluation = Evaluation(
            hypothesis = enrichedHypothesis,
            finalScore = enrichedHypothesis.rawConfidence,
            risk = risk,
            reasoning = "Shadow Evaluation"
        )

        // Decidir
        val decision = DecisionEngine.decide(evaluation, worldState)
        val duration = System.currentTimeMillis() - startTime

        // 2. Comparar y Loguear
        val isMatch = legacyIntent == decision.winningEvaluation?.hypothesis?.candidateGoal?.let {
             if (it.category == Goal.Category.MULTIMEDIA) Intent.PLAY_MUSIC else Intent.UNKNOWN
        }

        val result = ShadowResult(
            input = text,
            thamisDecision = decision,
            legacyIntent = legacyIntent,
            isMatch = isMatch ?: false,
            processingTimeMs = duration,
            trace = decision.trace
        )

        ShadowLogger.log(result)
    }
}
