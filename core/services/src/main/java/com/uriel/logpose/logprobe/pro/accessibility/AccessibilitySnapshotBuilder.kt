package com.uriel.logpose.logprobe.accessibility

import android.view.accessibility.AccessibilityEvent
import com.uriel.logpose.logprobe.common.ProbeClock
import com.uriel.logpose.logprobe.common.ProbeGuard
import com.uriel.logpose.logprobe.models.AccessibilitySnapshot

/**
 * Punto de entrada para construir un AccessibilitySnapshot a partir de
 * un AccessibilityEvent del SO. Es el unico lugar donde se decide si
 * vale la pena siquiera construir el arbol: si el paquete no esta
 * permitido, se corta aca y no se llega a tocar AccessibilityNodeInfo.
 */
object AccessibilitySnapshotBuilder {

    fun build(event: AccessibilityEvent): AccessibilitySnapshot? {
        val packageName = AccessibilityEventParser.packageNameOf(event)
        if (!ProbeGuard.isPackageAllowed(packageName)) {
            return null
        }

        val source = event.source
        val rootSnapshot = try {
            AccessibilityNodePrinter.print(source, packageName)
        } finally {
            source?.recycle()
        }

        return AccessibilitySnapshot(
            packageName = packageName.orEmpty(),
            className = AccessibilityEventParser.classNameOf(event),
            eventType = AccessibilityEventParser.eventTypeNameOf(event),
            timestampMillis = ProbeClock.nowMillis(),
            rootNode = rootSnapshot
        )
    }
}
