package com.uriel.logpose.thamis.notification.model

import java.util.*

/**
 * Categorías de notificación reconocidas por THAMIS.
 */
enum class NotificationCategory {
    MESSAGE, CALL, SOCIAL, SYSTEM, NAVIGATION, DELIVERY, 
    CALENDAR, REMINDER, BATTERY, SECURITY, EMERGENCY, UNKNOWN
}

/**
 * Niveles de prioridad para la planificación de salida de audio.
 */
enum class NotificationPriority {
    CRITICAL, HIGH, NORMAL, LOW, SILENT
}

/**
 * Contexto de una notificación entrante (abstraído de Android).
 */
data class NotificationContext(
    val id: String = UUID.randomUUID().toString(),
    val appPackage: String,
    val title: String?,
    val text: String?,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Decisión final del motor de notificaciones.
 */
data class NotificationDecision(
    val id: String = UUID.randomUUID().toString(),
    val category: NotificationCategory,
    val priority: NotificationPriority,
    val action: Action,
    val reason: String,
    val confidence: Float
) {
    enum class Action {
        READ_NOW, WAIT, GROUP, SILENCE, IGNORE, POSTPONE, CANCEL
    }
}

/**
 * Resultado de una agrupación de notificaciones.
 */
data class NotificationSummary(
    val count: Int,
    val description: String,
    val isMultiPerson: Boolean = false
)

data class NotificationGoal(
    val intent: String,
    val targetApp: String?,
    val confidence: Float
)

data class NotificationStatistics(
    val totalReceived: Int,
    val criticalCount: Int,
    val ignoredCount: Int,
    val averageLatency: Long
)
