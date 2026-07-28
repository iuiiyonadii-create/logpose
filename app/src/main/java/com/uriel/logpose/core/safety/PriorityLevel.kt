package com.uriel.logpose.core.safety

/**
 * Priority levels for system notifications and alerts.
 */
enum class PriorityLevel {
    CRITICAL,   // Navigation turns, incoming calls, safety alerts
    IMPORTANT,  // Important messages, arriving at destination
    INFORMATIONAL // System status, low-priority notifications
}
