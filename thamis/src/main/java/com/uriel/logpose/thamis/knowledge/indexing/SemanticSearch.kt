package com.uriel.logpose.thamis.knowledge.indexing

import com.uriel.logpose.thamis.knowledge.graph.GraphNode
import com.uriel.logpose.thamis.knowledge.repository.KnowledgeRepository

/**
 * FASE 27.1 — THAMIS KNOWLEDGE GRAPH
 * FASE 13: SEMANTIC SEARCH
 */
object SemanticSearch {

    fun search(query: String): List<GraphNode> {
        return KnowledgeRepository.getAllExperience().filter { 
            it.id.contains(query, ignoreCase = true) 
        }
    }
}
