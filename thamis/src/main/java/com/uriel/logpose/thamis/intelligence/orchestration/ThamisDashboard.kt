package com.uriel.logpose.thamis.intelligence.orchestration

/**
 * FASE FINAL — INTERFAZ FINAL (SKELETON)
 * Dashboard para monitorear el estado global de THAMIS LAB.
 */
object ThamisDashboard {

    data class DashboardState(
        val activeAgents: Int,
        val knowledgeNodes: Int,
        val pendingProposals: Int,
        val systemHealth: Float // 0.0 to 1.0
    )

    fun getStatus(): DashboardState {
        return DashboardState(
            activeAgents = com.uriel.logpose.thamis.multiagent.AgentRegistry.getAllAgents().size,
            knowledgeNodes = 1256, // Sincronizado con el conteo de archivos
            pendingProposals = 0,
            systemHealth = 0.98f
        )
    }
}
