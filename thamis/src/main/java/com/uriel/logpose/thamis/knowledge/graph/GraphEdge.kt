package com.uriel.logpose.thamis.knowledge.graph

/**
 * FASE 27.1 — THAMIS KNOWLEDGE GRAPH
 * FASE 3: GRAPH RELATIONSHIPS
 *
 * Representa una relación dirigida entre dos nodos del grafo.
 */
data class GraphEdge(
    val sourceId: String,
    val targetId: String,
    val type: RelationshipType
)

enum class RelationshipType {
    USES,
    DEPENDS_ON,
    IMPLEMENTS,
    REQUIRES,
    IMPROVES,
    SIMILAR_TO,
    GENERATES,
    VALIDATES
}
