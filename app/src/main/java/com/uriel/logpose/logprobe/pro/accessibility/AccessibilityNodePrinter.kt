package com.uriel.logpose.logprobe.accessibility

import android.view.accessibility.AccessibilityNodeInfo
import com.uriel.logpose.logprobe.common.ProbeGuard
import com.uriel.logpose.logprobe.common.ProbeUtils
import com.uriel.logpose.logprobe.models.AccessibilityNodeSnapshot

/**
 * Convierte un AccessibilityNodeInfo en un AccessibilityNodeSnapshot,
 * SOLO si ProbeGuard aprueba cada nodo individualmente.
 *
 * Regla dura: si node.isPassword es true, o el nodo pertenece a un
 * paquete no permitido, el texto NUNCA se lee. No se lee y se descarta
 * despues: directamente no se invoca node.text / node.contentDescription
 * en esos casos, para que el dato nunca exista en memoria del proceso.
 *
 * Limite de profundidad para evitar arboles gigantes o recursion
 * patologica en apps con jerarquias muy anidadas.
 */
object AccessibilityNodePrinter {

    private const val MAX_DEPTH = 12
    private const val MAX_CHILDREN_PER_NODE = 50

    fun print(
        node: AccessibilityNodeInfo?,
        packageName: String?,
        depth: Int = 0
    ): AccessibilityNodeSnapshot? {
        if (node == null) return null
        if (depth > MAX_DEPTH) return null

        val allowed = ProbeGuard.isAccessibilityNodeAllowed(
            packageName = packageName,
            isPasswordField = node.isPassword,
            fieldHint = node.viewIdResourceName
        )

        val text: String?
        val description: String?
        if (allowed) {
            text = ProbeUtils.safeTrim(node.text)
            description = ProbeUtils.safeTrim(node.contentDescription)
        } else {
            // Nodo no permitido: no se lee texto ni descripcion. Se puede
            // seguir bajando por los hijos (otro hijo podria pertenecer
            // a una parte no sensible), pero este nodo en particular
            // nunca aporta contenido textual.
            text = null
            description = null
        }

        val children = mutableListOf<AccessibilityNodeSnapshot>()
        val childCount = minOf(node.childCount, MAX_CHILDREN_PER_NODE)
        for (i in 0 until childCount) {
            val child = node.getChild(i) ?: continue
            try {
                print(child, packageName, depth + 1)?.let { children.add(it) }
            } finally {
                child.recycle()
            }
        }

        return AccessibilityNodeSnapshot(
            className = node.className?.toString(),
            text = text,
            contentDescription = description,
            viewIdResourceName = node.viewIdResourceName,
            childCount = node.childCount,
            children = children
        )
    }
}
