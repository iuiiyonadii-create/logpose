package com.uriel.logpose.logprobe.notification

import com.uriel.logpose.logprobe.models.NotificationSnapshot
import com.uriel.logpose.logprobe.models.ProbeEventType
import com.uriel.logpose.logprobe.reports.ProbeTimeline

/**
 * Vista de solo lectura sobre las notificaciones ya registradas en
 * ProbeTimeline durante la sesion activa. No mantiene su propio buffer:
 * ProbeTimeline es la unica fuente de verdad en memoria.
 */
object NotificationHistory {

    fun all(): List<NotificationSnapshot> =
        ProbeTimeline.snapshot()
            .filter {
                it.type == ProbeEventType.NOTIFICATION_POSTED ||
                    it.type == ProbeEventType.NOTIFICATION_REMOVED
            }
            .mapNotNull { it.snapshot as? NotificationSnapshot }

    fun byPackage(packageName: String): List<NotificationSnapshot> =
        all().filter { it.packageName == packageName }
}
