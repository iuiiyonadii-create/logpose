package com.uriel.logpose.thamis.notification.priority

import com.uriel.logpose.thamis.notification.model.NotificationCategory
import com.uriel.logpose.thamis.notification.model.NotificationPriority

/**
 * Motor de priorización de notificaciones para el canal de audio.
 */
object NotificationPriorityEngine {

    fun determinePriority(category: NotificationCategory): NotificationPriority {
        return when (category) {
            NotificationCategory.EMERGENCY -> NotificationPriority.CRITICAL
            NotificationCategory.NAVIGATION -> NotificationPriority.HIGH
            NotificationCategory.CALL -> NotificationPriority.HIGH
            NotificationCategory.MESSAGE -> NotificationPriority.NORMAL
            NotificationCategory.DELIVERY -> NotificationPriority.NORMAL
            NotificationCategory.BATTERY -> NotificationPriority.HIGH
            NotificationCategory.SOCIAL -> NotificationPriority.LOW
            else -> NotificationPriority.LOW
        }
    }
}
