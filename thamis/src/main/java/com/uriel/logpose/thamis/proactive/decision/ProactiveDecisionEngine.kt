package com.uriel.logpose.thamis.proactive.decision

import com.uriel.logpose.thamis.proactive.model.ProactiveAction
import com.uriel.logpose.thamis.proactive.model.ProactiveDecision
import com.uriel.logpose.thamis.proactive.model.Suggestion

/**
 * Decide si una sugerencia debe ser presentada al usuario.
 */
object ProactiveDecisionEngine {

    fun decide(suggestion: Suggestion, cognitiveLoad: Float): ProactiveDecision {
        return when {
            suggestion.urgency > 90 -> ProactiveDecision(ProactiveAction.SUGGEST, "Urgencia máxima", 1.0f, 100, null)
            cognitiveLoad > 0.8f -> ProactiveDecision(ProactiveAction.WAIT, "Carga cognitiva elevada", 0.9f, 50, null)
            suggestion.urgency > 50 -> ProactiveDecision(ProactiveAction.SUGGEST, "Valor aportado alto", 0.85f, 60, null)
            else -> ProactiveDecision(ProactiveAction.IGNORE, "Prioridad insuficiente", 0.4f, 10, null)
        }
    }
}
