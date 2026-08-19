package com.uriel.logpose.features.notifications

import android.service.notification.StatusBarNotification
import com.uriel.logpose.domain.notifications.NotificationCategory
import com.uriel.logpose.domain.notifications.NotificationEvent
import com.uriel.logpose.domain.notifications.NotificationPriority

/**
 * Parses Android SBN into domain NotificationEvent.
 */
class NotificationParser {

    fun parse(sbn: StatusBarNotification): NotificationEvent {
        val extras = sbn.notification.extras
        val title = extras.getString("android.title") ?: ""
        val text = extras.getCharSequence("android.text")?.toString() ?: ""
        val packageName = sbn.packageName

        return NotificationEvent(
            id = sbn.id.toString(),
            application = packageName,
            title = title,
            content = text,
            category = mapPackageToCategory(packageName),
            priority = NotificationPriority.NORMAL // To be refined by Analyzer
        )
    }

    private fun mapPackageToCategory(packageName: String): NotificationCategory {
        return when {
            packageName.contains("whatsapp") || packageName.contains("telegram") -> NotificationCategory.MESSAGE
            packageName.contains("maps") || packageName.contains("waze") -> NotificationCategory.NAVIGATION
            packageName.contains("dialer") || packageName.contains("phone") -> NotificationCategory.CALL
            else -> NotificationCategory.OTHER
        }
    }
}
