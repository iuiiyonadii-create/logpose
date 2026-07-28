package com.uriel.logpose.thamis.multiagent

/**
 * FASE 27.2 — THAMIS MULTI-AGENT COLLABORATION ENGINE
 * FASE 2: AGENT REGISTRY
 */
object AgentRegistry {
    private val agents = mutableMapOf<String, Agent>()

    fun register(agent: Agent) {
        agents[agent.name] = agent
    }

    fun getAgent(name: String): Agent? = agents[name]

    fun getAllAgents(): List<Agent> = agents.values.toList()
}
