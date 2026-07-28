package com.uriel.logpose.thamis.notification.validation

/**
 * Recolector de métricas para el motor de notificaciones.
 */
object NotificationMetrics {
    var totalProcessed = 0
    var criticalCount = 0
    var ignoredCount = 0
    var averageLatencyMs = 0L

    fun record(priority: com.uriel.logpose.thamis.notification.model.NotificationPriority, latency: Long) {
        totalProcessed++
        if (priority == com.uriel.logpose.thamis.notification.model.NotificationPriority.CRITICAL) criticalCount++
        averageLatencyMs = (averageLatencyMs * (totalProcessed - 1) + latency) / totalProcessed
    }
}
