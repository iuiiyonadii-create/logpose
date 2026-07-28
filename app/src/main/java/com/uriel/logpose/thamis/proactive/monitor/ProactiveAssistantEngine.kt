package com.uriel.logpose.thamis.proactive.monitor

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.proactive.decision.ProactiveDecisionEngine
import com.uriel.logpose.thamis.proactive.model.ProactiveAction
import com.uriel.logpose.thamis.proactive.model.ProactiveDecision
import com.uriel.logpose.thamis.proactive.suggestion.SuggestionEngine
import com.uriel.logpose.thamis.proactive.timing.CommunicationTimingEngine
import com.uriel.logpose.thamis.world.engine.WorldModelEngine

/**
 * Motor central del asistente proactivo v1.0.
 */
object ProactiveAssistantEngine {

    fun runCycle(): List<ProactiveDecision> {
        val world = WorldModelEngine.getCurrentSnapshot()
        val decisions = mutableListOf<ProactiveDecision>()

        // 1. Generar sugerencias
        val suggestions = SuggestionEngine.generate(world)

        // 2. Evaluar cada sugerencia
        suggestions.forEach { suggestion ->
            val decision = ProactiveDecisionEngine.decide(suggestion, 0.2f) // Carga cognitiva placeholder
            
            // 3. Validar timing si la decisión es sugerir
            if (decision.action == ProactiveAction.SUGGEST) {
                if (CommunicationTimingEngine.isGoodTime(world)) {
                    decisions.add(decision)
                    LogPoseLogger.i("THAMIS_PROACTIVE: Sugerencia aprobada: ${suggestion.message}")
                } else {
                    decisions.add(decision.copy(action = ProactiveAction.WAIT, reason = "Pausado por mal timing"))
                }
            } else {
                decisions.add(decision)
            }
        }

        return decisions
    }
}
