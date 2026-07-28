package com.uriel.logpose.thamis.knowledge.indexing

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.knowledge.graph.GraphNode
import com.uriel.logpose.thamis.knowledge.graph.KnowledgeGraph
import com.uriel.logpose.thamis.knowledge.graph.RelationshipEngine

/**
 * FASE 27.1 — THAMIS KNOWLEDGE GRAPH
 * FASE 14: KNOWLEDGE INDEXER
 */
object KnowledgeIndexer {

    fun indexComponent(node: GraphNode) {
        LogPoseLogger.i("KnowledgeIndexer: Indexando componente ${node.id}")
        KnowledgeGraph.addNode(node)
        RelationshipEngine.inferRelationships(node)
    }
}
