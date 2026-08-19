package com.uriel.logpose.logprobe.accessibility

import com.uriel.logpose.logprobe.models.AccessibilityNodeSnapshot

/**
 * Utilidades de solo lectura sobre un arbol ya construido
 * (AccessibilityNodeSnapshot). No toca AccessibilityNodeInfo del SO:
 * eso es responsabilidad exclusiva de AccessibilityNodePrinter.
 */
object AccessibilityTree {

    fun countNodes(root: AccessibilityNodeSnapshot?): Int {
        if (root == null) return 0
        return 1 + root.children.sumOf { countNodes(it) }
    }

    fun flatten(root: AccessibilityNodeSnapshot?): List<AccessibilityNodeSnapshot> {
        if (root == null) return emptyList()
        return listOf(root) + root.children.flatMap { flatten(it) }
    }
}
