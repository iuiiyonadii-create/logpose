package com.uriel.logpose.logprobe.notification

import android.app.Notification
import android.service.notification.StatusBarNotification
import com.uriel.logpose.domain.models.NotificationContent
import com.uriel.logpose.logprobe.models.NotificationSnapshot

/**
 * Extrae metadatos y, opcionalmente, contenido de una notificación.
 *
 * parse(): usado por LogProbe (solo metadatos).
 * parseFull(): usado por Notification Core (lee contenido).
 */
object NotificationParser {

    fun parse(sbn: StatusBarNotification): NotificationSnapshot {
        val notification = sbn.notification

        return NotificationSnapshot(
            packageName = sbn.packageName,
            postedAtMillis = sbn.postTime,
            category = notification.category,
            isOngoing = sbn.isOngoing,
            isClearable = sbn.isClearable,
            actionCount = notification.actions?.size ?: 0,
            hasContentIntent = notification.contentIntent != null
        )
    }

    fun parseFull(
        sbn: StatusBarNotification,
        removed: Boolean = false
    ): NotificationContent {
        val notification = sbn.notification
        val extras = notification.extras

        return NotificationContent(
            key = sbn.key,
            packageName = sbn.packageName,
            postedAtMillis = sbn.postTime,
            title = extras?.getCharSequence(Notification.EXTRA_TITLE)?.toString(),
            text = extras?.getCharSequence(Notification.EXTRA_TEXT)?.toString(),
            subText = extras?.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString(),
            bigText = extras?.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString(),
            systemCategory = notification.category,
            isOngoing = sbn.isOngoing,
            isClearable = sbn.isClearable,
            actionCount = notification.actions?.size ?: 0,
            hasContentIntent = notification.contentIntent != null,
            removed = removed
        )
    }
}