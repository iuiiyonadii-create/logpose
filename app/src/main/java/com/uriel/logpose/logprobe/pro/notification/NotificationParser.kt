package com.uriel.logpose.logprobe.notification

import android.service.notification.StatusBarNotification
import com.uriel.logpose.logprobe.models.NotificationSnapshot

/**
 * Extrae UNICAMENTE metadatos estructurales de una notificacion.
 * Nunca lee extras de texto (EXTRA_TEXT, EXTRA_TITLE, EXTRA_BIG_TEXT,
 * etc.): esos campos ni siquiera se tocan aca, para que el contenido
 * de la notificacion nunca exista en memoria de LogProbe.
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
}
