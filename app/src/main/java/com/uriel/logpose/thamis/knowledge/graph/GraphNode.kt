package com.uriel.logpose.thamis.knowledge.graph

/**
 * FASE 27.1 — THAMIS KNOWLEDGE GRAPH
 * FASE 2: GRAPH NODES
 *
 * Representa un nodo dentro del grafo de conocimiento de ingeniería.
 */
data class GraphNode(
    val id: String,
    val type: NodeType,
    val properties: Map<String, String> = emptyMap()
)

enum class NodeType {
    PROJECT,
    MODULE,
    FEATURE,
    TECHNOLOGY,
    PATTERN,
    PROBLEM,
    SOLUTION,
    TEST,
    DOCUMENTATION
}
