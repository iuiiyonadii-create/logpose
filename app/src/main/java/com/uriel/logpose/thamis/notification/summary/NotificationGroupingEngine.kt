package com.uriel.logpose.thamis.notification.summary

import com.uriel.logpose.thamis.notification.model.NotificationContext

/**
 * Agrupa notificaciones similares para evitar fatiga auditiva.
 */
object NotificationGroupingEngine {

    private val windowMs = 5000L // Ventana de 5 segundos para agrupar

    fun group(pending: List<NotificationContext>): List<List<NotificationContext>> {
        if (pending.isEmpty()) return emptyList()

        return pending.groupBy { it.appPackage }.values.toList()
    }
}
