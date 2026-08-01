package com.thamis.lab.intelligence.graph

import java.util.concurrent.ConcurrentHashMap

public data class GraphNode(
    public val nodeId: String,
    public val nodeType: String,
    public val label: String
)

public data class GraphEdge(
    public val sourceId: String,
    public val targetId: String,
    public val relation: String
)

/**
 * Knowledge Graph connecting Errors, Events, Dependencies, Scenarios, and Devices.
 */
public class KnowledgeGraph {
    private val nodes = ConcurrentHashMap<String, GraphNode>()
    private val edges = ConcurrentHashMap<String, GraphEdge>()

    public fun addNode(node: GraphNode) {
        nodes[node.nodeId] = node
    }

    public fun addEdge(edge: GraphEdge) {
        val key = "${edge.sourceId}->${edge.targetId}"
        edges[key] = edge
    }

    public fun getRelatedNodes(nodeId: String): List<GraphNode> {
        val targetIds = edges.values.filter { it.sourceId == nodeId }.map { it.targetId }
        return nodes.values.filter { targetIds.contains(it.nodeId) }
    }

    public fun totalNodes(): Int = nodes.size
    public fun totalEdges(): Int = edges.size

    public fun clear() {
        nodes.clear()
        edges.clear()
    }
}
