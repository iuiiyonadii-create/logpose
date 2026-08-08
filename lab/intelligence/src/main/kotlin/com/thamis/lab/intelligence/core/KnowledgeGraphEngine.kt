package com.thamis.lab.intelligence.core

import com.thamis.lab.core.common.logging.LabLogger

/**
 * Knowledge Graph Engine maintaining an indexed graph of modules, classes, devices, scenarios, and bug relationships.
 */
public class KnowledgeGraphEngine(
    public val graph: KnowledgeGraph = KnowledgeGraph()
) {
    private val TAG = "KnowledgeGraphEngine"

    public fun addNode(node: GraphNode) {
        graph.addNode(node)
        LabLogger.info(TAG, "Added Knowledge Graph Node: ${node.nodeId} (${node.nodeType})")
    }

    public fun addEdge(edge: GraphEdge) {
        graph.addEdge(edge)
        LabLogger.info(TAG, "Added Knowledge Graph Edge: ${edge.sourceId} -> ${edge.targetId}")
    }

    public fun getTotalNodeCount(): Int = graph.totalNodes()
}
