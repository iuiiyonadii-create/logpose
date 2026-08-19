package com.uriel.logpose.logprobe.models

/**
 * Representa un nodo del arbol de Accessibility ya filtrado.
 * `text` y `contentDescription` solo estan presentes si ProbeGuard
 * determino que el nodo no es sensible; si un nodo fue descartado,
 * directamente no se genera un AccessibilityNodeSnapshot para el.
 */
data class AccessibilityNodeSnapshot(
    val className: String?,
    val text: String?,
    val contentDescription: String?,
    val viewIdResourceName: String?,
    val childCount: Int,
    val children: List<AccessibilityNodeSnapshot> = emptyList()
)

data class AccessibilitySnapshot(
    val packageName: String,
    val className: String?,
    val eventType: String,
    val timestampMillis: Long,
    val rootNode: AccessibilityNodeSnapshot?
)
