package com.uriel.logpose.thamis.knowledge.graph

/**
 * FASE 27.1 — THAMIS KNOWLEDGE GRAPH
 * FASE 1: KNOWLEDGE GRAPH CORE
 *
 * Motor central para gestionar el grafo de conocimiento técnico.
 */
object KnowledgeGraph {
    private val nodes = mutableMapOf<String, GraphNode>()
    private val edges = mutableListOf<GraphEdge>()

    fun addNode(node: GraphNode) {
        nodes[node.id] = node
    }

    fun addRelationship(sourceId: String, targetId: String, type: RelationshipType) {
        edges.add(GraphEdge(sourceId, targetId, type))
    }

    fun findNode(id: String): GraphNode? = nodes[id]

    fun getRelationshipsFor(nodeId: String): List<GraphEdge> {
        return edges.filter { it.sourceId == nodeId || it.targetId == nodeId }
    }

    fun clear() {
        nodes.clear()
        edges.clear()
    }
}
