package com.uriel.logpose.logprobe.collectors

import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
import com.uriel.logpose.logprobe.accessibility.AccessibilityEventParser
import com.uriel.logpose.logprobe.accessibility.AccessibilitySnapshotBuilder
import com.uriel.logpose.logprobe.common.ProbeClock
import com.uriel.logpose.logprobe.common.ProbeGuard
import com.uriel.logpose.logprobe.common.ProbeUtils
import com.uriel.logpose.logprobe.models.ProbeEvent
import com.uriel.logpose.logprobe.models.ProbeEventType
import com.uriel.logpose.logprobe.models.WindowSnapshot
import com.uriel.logpose.logprobe.reports.ProbeTimeline

/**
 * Puente entre AccessibilityProbeService y el resto del modulo.
 * Cada evento pasa primero por AccessibilitySnapshotBuilder (que ya
 * aplica ProbeGuard); si el resultado es null, el evento simplemente
 * no se registra.
 *
 * Tambien deriva WindowCollector: los cambios de ventana activa se
 * detectan a partir de TYPE_WINDOW_STATE_CHANGED, evitando pedir el
 * permiso especial de UsageStats para algo que Accessibility ya sabe.
 */
object AccessibilityCollector : ServiceBackedCollector {

    private var connected = false

    override fun onServiceConnected() {
        connected = true
    }

    override fun onServiceDisconnected() {
        connected = false
    }

    override fun isActive(): Boolean = connected

    fun onAccessibilityEvent(event: AccessibilityEvent) {
        val session = ProbeGuard.currentSession() ?: return
        val packageName = AccessibilityEventParser.packageNameOf(event)

        val snapshot = AccessibilitySnapshotBuilder.build(event) ?: run {
            // Paquete no permitido o snapshot vacio: no se registra nada.
            return
        }

        ProbeTimeline.record(
            ProbeEvent(
                id = ProbeUtils.newEventId(),
                sessionId = session.sessionId,
                type = ProbeEventType.ACCESSIBILITY_EVENT,
                timestampMillis = ProbeClock.nowMillis(),
                sourcePackage = packageName.orEmpty(),
                snapshot = snapshot
            )
        )

        if (event.eventType == TYPE_WINDOW_STATE_CHANGED) {
            recordWindowChange(session.sessionId, packageName, event)
        }
    }

    private fun recordWindowChange(
        sessionId: String,
        packageName: String?,
        event: AccessibilityEvent
    ) {
        if (!ProbeGuard.isPackageAllowed(packageName)) return
        ProbeTimeline.record(
            ProbeEvent(
                id = ProbeUtils.newEventId(),
                sessionId = sessionId,
                type = ProbeEventType.WINDOW_CHANGED,
                timestampMillis = ProbeClock.nowMillis(),
                sourcePackage = packageName.orEmpty(),
                snapshot = WindowSnapshot(
                    timestampMillis = ProbeClock.nowMillis(),
                    packageName = packageName.orEmpty(),
                    className = AccessibilityEventParser.classNameOf(event)
                )
            )
        )
    }
}
