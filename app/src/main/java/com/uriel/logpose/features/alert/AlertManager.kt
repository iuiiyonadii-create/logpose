package com.uriel.logpose.features.alert

import com.uriel.logpose.core.compat.core.LogPoseLogger

enum class AlertPriority {
    LOW,
    MEDIUM,
    HIGH
}

data class Alert(
    val title: String,
    val message: String,
    val priority: AlertPriority
)

object AlertManager {

    private val queue = mutableListOf<Alert>()

    fun enqueue(alert: Alert) {
        queue.add(alert)
        LogPoseLogger.w("[ALERT][${alert.priority}] ${alert.title}: ${alert.message}")
    }

    fun getPendingAlerts(): List<Alert> = queue.toList()

    fun clear() {
        queue.clear()
    }
}
