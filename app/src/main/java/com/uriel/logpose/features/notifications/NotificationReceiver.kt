package com.uriel.logpose.features.notifications

import android.service.notification.StatusBarNotification
import com.uriel.logpose.logprobe.notification.NotificationEventBus
import com.uriel.logpose.logprobe.notification.NotificationEventListener
import com.uriel.logpose.logprobe.notification.NotificationParser

/**
 * Punto de entrada de Notification Core al pipeline de notificaciones del
 * sistema.
 */
class NotificationReceiver(
    private val repository: NotificationRepository
) : NotificationEventListener {

    override fun onNotificationEvent(
        sbn: StatusBarNotification,
        removed: Boolean
    ) {
        val content = NotificationParser.parseFull(sbn, removed = removed)

        if (removed) {
            repository.onRemoved(content)
        } else {
            repository.onPosted(content)
        }
    }

    fun attach() {
        NotificationEventBus.register(this)
    }

    fun detach() {
        NotificationEventBus.unregister(this)
    }
}