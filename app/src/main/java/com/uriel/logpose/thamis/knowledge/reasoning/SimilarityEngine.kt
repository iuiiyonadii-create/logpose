package com.uriel.logpose.thamis.knowledge.reasoning

import com.uriel.logpose.thamis.knowledge.graph.GraphNode

/**
 * FASE 27.1 — THAMIS KNOWLEDGE GRAPH
 * FASE 11: SIMILARITY ENGINE
 */
object SimilarityEngine {

    fun calculateSimilarity(nodeA: GraphNode, nodeB: GraphNode): Double {
        if (nodeA.type == nodeB.type) {
            return 0.8 // Simulación básica
        }
        return 0.1
    }
}
