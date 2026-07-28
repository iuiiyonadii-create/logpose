package com.uriel.logpose.features.notifications

import com.uriel.logpose.domain.notifications.NotificationCategory
import com.uriel.logpose.domain.notifications.NotificationEvent
import com.uriel.logpose.domain.notifications.NotificationPriority

/**
 * Evaluates the importance of a notification based on content and context.
 */
class NotificationAnalyzer {

    fun analyze(event: NotificationEvent): NotificationPriority {
        return when (event.category) {
            NotificationCategory.CALL -> NotificationPriority.CRITICAL
            NotificationCategory.NAVIGATION -> NotificationPriority.CRITICAL
            NotificationCategory.MESSAGE -> {
                if (isUrgent(event.content)) NotificationPriority.IMPORTANT 
                else NotificationPriority.NORMAL
            }
            else -> NotificationPriority.IGNORE
        }
    }

    private fun isUrgent(content: String): Boolean {
        val keywords = listOf("urgente", "ayuda", "llamar", "importante")
        return keywords.any { content.lowercase().contains(it) }
    }
}
