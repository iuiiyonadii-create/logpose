package com.uriel.logpose.domain.repositories

/**
 * Contract for Notification operations.
 */
interface NotificationRepository {
    fun setNotificationReadingEnabled(enabled: Boolean)
    fun isReadingEnabled(): Boolean
}
