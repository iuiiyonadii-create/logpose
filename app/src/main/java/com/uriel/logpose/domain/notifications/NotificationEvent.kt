package com.uriel.logpose.domain.notifications

/**
 * Model representing a detected notification event.
 */
data class NotificationEvent(
    val id: String,
    val application: String,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val category: NotificationCategory,
    val priority: NotificationPriority
)

enum class NotificationCategory {
    MESSAGE,
    CALL,
    NAVIGATION,
    SYSTEM,
    DELIVERY,
    OTHER
}

enum class NotificationPriority {
    CRITICAL,
    IMPORTANT,
    NORMAL,
    IGNORE
}
