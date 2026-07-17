package com.uriel.logpose.features.notifications

import com.uriel.logpose.domain.models.NotificationCategory
import com.uriel.logpose.domain.models.NotificationContent
import com.uriel.logpose.domain.models.NotificationPriority

/**
 * Notificacion ya procesada por Notification Core: contenido crudo mas las
 * decisiones tomadas por el modulo (categoria, prioridad, estado de lectura).
 * Es lo que consume NotificationState / la UI.
 */
data class NotificationItem(
    val id: String,
    val content: NotificationContent,
    val category: NotificationCategory,
    val priority: NotificationPriority,
    val isRead: Boolean = false
) {
    val packageName: String get() = content.packageName
    val postedAtMillis: Long get() = content.postedAtMillis

    companion object {
        fun from(content: NotificationContent): NotificationItem {
            val category = NotificationCategory.classify(content.packageName, content.systemCategory)
            val priority = NotificationPriority.fromContent(
                category = category,
                isOngoing = content.isOngoing,
                hasContentIntent = content.hasContentIntent
            )
            return NotificationItem(
                id = content.key,
                content = content,
                category = category,
                priority = priority
            )
        }
    }
}
