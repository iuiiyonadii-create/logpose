package com.uriel.logpose.features.notifications

/**
 * Estado observable por la UI (Compose) via NotificationCoreManager.
 */
data class NotificationState(
    val items: List<NotificationItem> = emptyList(),
    val unreadCount: Int = 0,
    val enabled: Boolean = true,
    val drivingModeEnabled: Boolean = false,
    val whitelist: Set<String> = emptySet(),
    val blacklist: Set<String> = emptySet()
)
