package com.uriel.logpose.thamis.multiagent

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * FASE 27.2 — THAMIS MULTI-AGENT COLLABORATION ENGINE
 * FASE 11: AGENT COORDINATOR
 */
object AgentCoordinator {

    fun distributeTask(task: String, agentName: String): String {
        val agent = AgentRegistry.getAgent(agentName)
        return if (agent != null) {
            LogPoseLogger.d("AgentCoordinator: Tarea enviada a $agentName")
            agent.execute(task)
        } else {
            "Agente $agentName no encontrado."
        }
    }
}
