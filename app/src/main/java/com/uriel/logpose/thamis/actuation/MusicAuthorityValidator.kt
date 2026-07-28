package com.uriel.logpose.thamis.actuation

import com.uriel.logpose.thamis.cognitive.model.ThamisDecision
import com.uriel.logpose.thamis.cognitive.model.WorldState
import com.uriel.logpose.thamis.cognitive.model.Goal
import com.uriel.logpose.thamis.safety.SafetyGate
import com.uriel.logpose.thamis.security.ThamisAuthorityGate

/**
 * Validador específico para la autoridad del dominio MULTIMEDIA.
 */
object MusicAuthorityValidator {

    fun validate(decision: ThamisDecision, worldState: WorldState): Boolean {
        val winningEval = decision.winningEvaluation ?: return false
        val goal = winningEval.hypothesis.candidateGoal
        
        // 1. Verificar dominio MULTIMEDIA
        if (goal.category != Goal.Category.MULTIMEDIA) return false
        
        // 2. Verificar autoridad específica del dominio
        if (!ThamisAuthorityGate.canExecute(ThamisAuthorityGate.Domain.MULTIMEDIA)) return false

        // 3. Verificar umbrales de confianza específicos para música
        val confidence = winningEval.finalScore
        val requiredConfidence = when (decision.intent) {
            com.uriel.logpose.thamis.intent.Intent.PLAY_MUSIC -> 0.65f
            com.uriel.logpose.thamis.intent.Intent.NEXT_TRACK, com.uriel.logpose.thamis.intent.Intent.PREVIOUS_TRACK -> 0.60f
            com.uriel.logpose.thamis.intent.Intent.SET_VOLUME -> 0.50f
            else -> 0.70f
        }
        
        if (confidence < requiredConfidence) return false

        // 4. Verificar SafetyGate (Riesgo vs Velocidad)
        return SafetyGate.isApproved(decision, worldState)
    }
}
