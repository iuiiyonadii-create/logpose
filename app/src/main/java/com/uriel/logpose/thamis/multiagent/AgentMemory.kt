package com.uriel.logpose.thamis.multiagent

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * FASE FINAL — MULTI AGENT SYSTEM
 * Responsable de que cada agente mantenga un historial de sus decisiones
 * y aprendizajes específicos en su área de especialización.
 */
class AgentMemory(private val agentName: String) {

    private val logs = mutableListOf<MemoryEntry>()

    data class MemoryEntry(
        val timestamp: Long,
        val task: String,
        val result: String,
        val successful: Boolean
    )

    fun store(task: String, result: String, successful: Boolean = true) {
        logs.add(MemoryEntry(System.currentTimeMillis(), task, result, successful))
        LogPoseLogger.d("AgentMemory ($agentName): Nueva entrada guardada. Total: ${logs.size}")
    }

    fun findSimilar(query: String): List<MemoryEntry> {
        return logs.filter { it.task.contains(query, ignoreCase = true) }
    }

    fun clear() {
        logs.clear()
    }
}
