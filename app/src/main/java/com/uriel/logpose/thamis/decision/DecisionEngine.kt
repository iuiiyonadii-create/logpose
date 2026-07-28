package com.uriel.logpose.thamis.decision

import com.uriel.logpose.core.Command
import com.uriel.logpose.core.Action
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.context.ContextEngine
import com.uriel.logpose.thamis.multiagent.ConsensusEngine

/**
 * FASE 26.12 — LOGPOSE THAMIS ADVANCED DECISION ENGINE
 * FASE 1: DECISION ENGINE CORE
 */
object DecisionEngine {

    /**
     * Evalúa un comando basándose en el contexto actual y las reglas de seguridad.
     */
    fun evaluate(command: Command): Action {
        LogPoseLogger.d("DecisionEngine: Evaluando comando $command")
        
        // 1. Validación Multi-Agente (FASE 27.2)
        val agents = listOf("MotoSafety", "SecurityGuard")
        val isApprovedByAgents = ConsensusEngine.reachConsensus(command.name, agents)
        
        if (!isApprovedByAgents) {
            LogPoseLogger.w("DecisionEngine: Comando rechazado por falta de consenso multi-agente.")
            return Action.VoiceResponse("Los agentes de seguridad no recomiendan esa acción ahora.")
        }

        // 2. Validar seguridad (Legado)
        if (!SafetyValidator.isSafe(command)) {
            LogPoseLogger.w("DecisionEngine: Comando rechazado por seguridad.")
            return Action.VoiceResponse("No es seguro realizar esa acción ahora.")
        }
// ...

        // 2. Resolver prioridad
        val priority = PriorityResolver.resolve(command)
        LogPoseLogger.d("DecisionEngine: Prioridad resuelta: $priority")

        // 3. Seleccionar acción final
        return ActionSelector.selectAction(command)
    }
}
