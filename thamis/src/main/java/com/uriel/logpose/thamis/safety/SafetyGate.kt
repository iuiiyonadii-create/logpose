package com.uriel.logpose.thamis.safety

import com.uriel.logpose.thamis.cognitive.model.ThamisDecision
import com.uriel.logpose.thamis.cognitive.model.WorldState
import com.uriel.logpose.thamis.cognitive.model.Goal
import com.uriel.logpose.thamis.ThamisConfiguration

/**
 * La última barrera antes de la ejecución física.
 * Evalúa si una decisión cognitiva cumple con los estándares de seguridad y confianza.
 */
object SafetyGate {

    fun isApproved(decision: ThamisDecision, worldState: WorldState): Boolean {
        if (ThamisConfiguration.shadowMode) return false

        val evaluation = decision.winningEvaluation ?: return false
        val goal = evaluation.hypothesis.candidateGoal
        val confidence = evaluation.finalScore
        val risk = evaluation.risk

        // 1. Verificación de Feature Flags
        val isFeatureEnabled = when (goal.category) {
            Goal.Category.MULTIMEDIA -> ThamisConfiguration.musicControlEnabled
            Goal.Category.COMMUNICATION -> ThamisConfiguration.callsEnabled
            Goal.Category.NAVIGATION -> ThamisConfiguration.navigationEnabled
            else -> false
        }
        if (!isFeatureEnabled) return false

        // 2. Umbrales de Confianza vs Riesgo
        // Exigencia dinámica según la velocidad
        val speedPenalty = if (worldState.driving.speedKmh > 80) 0.1f else 0f
        val requiredConfidence = 0.70f + speedPenalty

        if (confidence < requiredConfidence) return false

        // 3. Bloqueo por Conducción Crítica
        if (worldState.driving.speedKmh > 120 && risk.level > 0.5f) return false

        return decision.isConclusive
    }
}
