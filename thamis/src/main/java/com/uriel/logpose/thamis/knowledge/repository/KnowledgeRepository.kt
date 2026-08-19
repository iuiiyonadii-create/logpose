package com.uriel.logpose.thamis.knowledge.repository

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.knowledge.graph.GraphNode

/**
 * FASE 27.1 — THAMIS KNOWLEDGE GRAPH
 * FASE 8: KNOWLEDGE REPOSITORY
 */
object KnowledgeRepository {
    private val experienceStore = mutableListOf<GraphNode>()

    fun storeExperience(node: GraphNode) {
        experienceStore.add(node)
        LogPoseLogger.i("KnowledgeRepository: Experiencia almacenada: ${node.id}")
    }

    fun getAllExperience(): List<GraphNode> = experienceStore.toList()
}
