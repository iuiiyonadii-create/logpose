package com.uriel.logpose.thamis.multiagent

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * FASE 27.2 — THAMIS MULTI-AGENT COLLABORATION ENGINE
 * FASE 1: BASE AGENT
 *
 * Clase base para todos los agentes especializados de THAMIS.
 */
abstract class Agent(val name: String, val role: String) {

    protected val memory = AgentMemory(name)

    open fun initialize() {
        LogPoseLogger.i("Agent $name ($role): Inicializado.")
    }

    abstract fun execute(task: String): String

    open fun vote(proposal: String): Boolean {
        // Por defecto los agentes aprueban si no hay fallos obvios
        return !proposal.contains("ERROR") && !proposal.contains("UNSAFE")
    }

    fun postToBus(content: String) {
        CollaborationBus.postMessage(name, content)
    }

    fun report(result: String) {
        memory.store("LastTask", result)
        LogPoseLogger.i("Agent $name: Reportando resultado: $result")
    }
}
