package com.uriel.logpose.thamis.multiagent

import com.uriel.logpose.core.app.LogPoseApplication
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.data.memory.AgentMemoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * FASE FINAL — MULTI AGENT SYSTEM
 * Responsable de que cada agente mantenga un historial persistente de sus decisiones
 * y aprendizajes específicos en su área de especialización.
 */
class AgentMemory(private val agentName: String) {

    private val localLogs = mutableListOf<MemoryEntry>()
    private val scope = CoroutineScope(Dispatchers.IO)

    data class MemoryEntry(
        val timestamp: Long,
        val task: String,
        val result: String,
        val successful: Boolean
    )

    fun store(task: String, result: String, successful: Boolean = true) {
        val entry = MemoryEntry(System.currentTimeMillis(), task, result, successful)
        localLogs.add(entry)
        
        // Persistir en Room DB si la aplicación está inicializada
        try {
            scope.launch {
                val dao = LogPoseApplication.entryPoint.agentMemoryDao()
                dao.insert(
                    AgentMemoryEntity(
                        agentName = agentName,
                        timestamp = entry.timestamp,
                        task = entry.task,
                        result = entry.result,
                        successful = entry.successful
                    )
                )
            }
        } catch (e: Exception) {
            LogPoseLogger.d("AgentMemory ($agentName): Modo local activo (DB uninitialized).")
        }

        LogPoseLogger.d("AgentMemory ($agentName): Nueva entrada guardada. Total local: ${localLogs.size}")
    }

    fun findSimilar(query: String): List<MemoryEntry> {
        val localMatches = localLogs.filter { it.task.contains(query, ignoreCase = true) }
        if (localMatches.isNotEmpty()) return localMatches

        return try {
            runBlocking(Dispatchers.IO) {
                val dao = LogPoseApplication.entryPoint.agentMemoryDao()
                val dbEntities = dao.findSimilar(agentName, query)
                dbEntities.map { MemoryEntry(it.timestamp, it.task, it.result, it.successful) }
            }
        } catch (e: Exception) {
            localMatches
        }
    }

    fun clear() {
        localLogs.clear()
        try {
            scope.launch {
                LogPoseApplication.entryPoint.agentMemoryDao().clearAgent(agentName)
            }
        } catch (_: Exception) {}
    }
}
