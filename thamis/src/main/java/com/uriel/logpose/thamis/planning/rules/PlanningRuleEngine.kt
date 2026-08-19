package com.uriel.logpose.thamis.planning.rules

import com.uriel.logpose.thamis.planning.model.*
import com.uriel.logpose.thamis.world.model.WorldSnapshot

/**
 * Motor de reglas determinísticas para la validación y programación de planes.
 */
object PlanningRuleEngine {

    fun evaluate(plan: ExecutionPlan, world: WorldSnapshot): PlanningDecision {
        // Regla 1: Prioridad Crítica siempre ejecuta (Emergencia)
        if (plan.priority == PlanningPriority.CRITICAL) return PlanningDecision.EXECUTE_NOW

        // Regla 2: Esperar fin de llamada
        if (world.systems.communication.isCallActive) return PlanningDecision.WAIT

        // Regla 3: Esperar precisión GPS si el plan es de Navegación
        if (plan.goal.contains("NAV", ignoreCase = true) && world.systems.navigation.accuracyMeters > 50f) {
            return PlanningDecision.WAIT
        }

        // Regla 4: Cancelar si el contexto cambió drásticamente (Riesgo Crítico)
        if (world.vehicle.riskLevel.name == "CRITICAL" && plan.priority != PlanningPriority.CRITICAL) {
            return PlanningDecision.CANCEL
        }

        // Regla 5: Esperar fin de TTS (Simulado vía estado de conversación)
        if (world.cognitive.conversationState != "IDLE") return PlanningDecision.WAIT

        return PlanningDecision.EXECUTE_NOW
    }
}
