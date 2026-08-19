package com.uriel.logpose.logprobe.collectors

import android.service.notification.StatusBarNotification
import com.uriel.logpose.logprobe.common.ProbeClock
import com.uriel.logpose.logprobe.common.ProbeGuard
import com.uriel.logpose.logprobe.common.ProbeUtils
import com.uriel.logpose.logprobe.models.NotificationSnapshot
import com.uriel.logpose.logprobe.models.ProbeEvent
import com.uriel.logpose.logprobe.models.ProbeEventType
import com.uriel.logpose.logprobe.notification.NotificationParser
import com.uriel.logpose.logprobe.reports.ProbeTimeline

/**
 * Puente entre NotificationProbeService y el resto del modulo.
 * Aplica ProbeGuard ANTES de invocar NotificationParser: si el paquete
 * no esta permitido, ni siquiera se construye el NotificationSnapshot.
 */
object NotificationCollector : ServiceBackedCollector {

    private var connected = false

    override fun onServiceConnected() {
        connected = true
    }

    override fun onServiceDisconnected() {
        connected = false
    }

    override fun isActive(): Boolean = connected

    fun onPosted(sbn: StatusBarNotification) {
        val session = ProbeGuard.currentSession() ?: return
        if (!ProbeGuard.isNotificationAllowed(sbn.packageName)) return

        val snapshot = NotificationParser.parse(sbn)
        record(session.sessionId, ProbeEventType.NOTIFICATION_POSTED, sbn.packageName, snapshot)
    }

    fun onRemoved(sbn: StatusBarNotification) {
        val session = ProbeGuard.currentSession() ?: return
        if (!ProbeGuard.isNotificationAllowed(sbn.packageName)) return

        val snapshot = NotificationParser.parse(sbn).copy(removed = true)
        record(session.sessionId, ProbeEventType.NOTIFICATION_REMOVED, sbn.packageName, snapshot)
    }

    private fun record(
        sessionId: String,
        type: ProbeEventType,
        packageName: String,
        snapshot: NotificationSnapshot
    ) {
        ProbeTimeline.record(
            ProbeEvent(
                id = ProbeUtils.newEventId(),
                sessionId = sessionId,
                type = type,
                timestampMillis = ProbeClock.nowMillis(),
                sourcePackage = packageName,
                snapshot = snapshot
            )
        )
    }
}
