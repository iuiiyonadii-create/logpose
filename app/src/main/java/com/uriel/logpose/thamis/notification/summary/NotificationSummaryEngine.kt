package com.uriel.logpose.thamis.notification.summary

import com.uriel.logpose.thamis.notification.model.NotificationSummary

/**
 * Genera resúmenes hablados de conjuntos de notificaciones.
 */
object NotificationSummaryEngine {

    fun summarize(notifications: List<com.uriel.logpose.thamis.notification.model.NotificationContext>): NotificationSummary {
        val count = notifications.size
        if (count == 0) return NotificationSummary(0, "No hay novedades importantes.")
        if (count == 1) return NotificationSummary(1, "Tenés una notificación nueva.")

        val apps = notifications.map { it.appPackage }.distinct().size
        val description = if (apps > 1) {
            "Tenés $count notificaciones de varias aplicaciones."
        } else {
            "Tenés $count mensajes nuevos."
        }

        return NotificationSummary(count, description, isMultiPerson = apps > 1)
    }
}
