package com.uriel.logpose.logprobe.notification

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.uriel.logpose.logprobe.collectors.NotificationCollector
import com.uriel.logpose.logprobe.common.ProbeGuard
import com.uriel.logpose.logprobe.common.ProbeLogger

/**
 * Servicio de NotificationListener de LogProbe. Igual que
 * AccessibilityProbeService: puede estar habilitado en Ajustes en
 * cualquier momento, pero solo reenvia datos si hay una ProbeSession
 * activa. Nunca descarta ni modifica notificaciones ajenas; unicamente
 * observa metadatos (ver NotificationParser).
 */
class NotificationProbeService : NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()
        ProbeLogger.d("NotificationProbeService conectado")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return
        if (!ProbeGuard.hasActiveSession()) return
        NotificationCollector.onPosted(sbn)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        if (sbn == null) return
        if (!ProbeGuard.hasActiveSession()) return
        NotificationCollector.onRemoved(sbn)
    }
}
