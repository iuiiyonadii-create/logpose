package com.uriel.logpose.thamis.cognitive.decision

import com.uriel.logpose.thamis.cognitive.model.*
import com.uriel.logpose.thamis.intent.Intent
import java.util.UUID

import com.uriel.logpose.thamis.Constitution

/**
 * El Juez de THAMIS.
 * Cruza la evaluación de la hipótesis con el estado del mundo y el riesgo.
 */
object DecisionEngine {

    fun decide(evaluation: Evaluation, worldState: WorldState): ThamisDecision {
        val intent = mapGoalToIntent(evaluation.hypothesis.candidateGoal)

        // 1. Validar Constitucionalidad (Ley Superior)
        if (!Constitution.isActionAllowed(intent, worldState)) {
            return buildRejection(evaluation, "Violación constitucional: Acción no permitida por seguridad o prioridad.")
        }

        // 2. Validar si el contexto permite la acción (Ejemplo de seguridad específica)
        if (worldState.driving.speedKmh > 120 && 
            evaluation.risk.type == com.uriel.logpose.thamis.cognitive.model.Risk.Type.PHYSICAL) {
            return buildRejection(evaluation, "Velocidad extrema impide acciones de alto riesgo físico.")
        }

        // 2. Aplicar política de decisión
        val action = DecisionPolicy.getActionType(evaluation.finalScore, evaluation.risk)

        // 3. Generar veredicto
        val isConclusive = action == "EXECUTE" || action == "IGNORE"

        return ThamisDecision(
            winningEvaluation = evaluation,
            intent = mapGoalToIntent(evaluation.hypothesis.candidateGoal),
            summary = "Decisión: $action | Confianza: ${evaluation.finalScore} | Riesgo: ${evaluation.risk.level}",
            isConclusive = isConclusive,
            trace = createSafeTrace(evaluation)
        )
    }

    private fun mapGoalToIntent(goal: Goal): Intent {
        // En una implementación futura, el Goal tendrá el Intent específico.
        // Por ahora, usamos el targetState como pista si no es UNKNOWN.
        return when (goal.category) {
            Goal.Category.MULTIMEDIA -> Intent.PLAY_MUSIC
            Goal.Category.NAVIGATION -> Intent.NAVIGATE
            Goal.Category.COMMUNICATION -> Intent.CALL_CONTACT
            else -> Intent.UNKNOWN
        }
    }

    private fun createSafeTrace(evaluation: Evaluation): CognitiveTrace {
        val now = System.currentTimeMillis()
        return CognitiveTrace(
            id = UUID.randomUUID().toString(),
            engineVersion = "THAMIS_v3.0_HARDENED",
            startTime = now,
            endTime = now,
            durationMs = 0,
            steps = listOf("DecisionEngine_Hardening"),
            hypotheses = listOf(evaluation.hypothesis),
            evaluations = listOf(evaluation),
            evidencesEvaluated = evaluation.hypothesis.evidences.size,
            rulesApplied = listOf("Safety_Audit_v1")
        )
    }

    private fun buildRejection(evaluation: Evaluation, reason: String): ThamisDecision {
        return ThamisDecision(
            winningEvaluation = evaluation,
            intent = Intent.UNKNOWN,
            summary = "REJECTED: $reason",
            isConclusive = true,
            trace = createSafeTrace(evaluation)
        )
    }
}
