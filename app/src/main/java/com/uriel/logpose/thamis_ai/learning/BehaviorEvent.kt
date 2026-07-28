package com.uriel.logpose.thamis_ai.learning

/**
 * Capture of a user action for learning purposes.
 */
data class BehaviorEvent(
    val eventType: BehaviorEventType,
    val timestamp: Long = System.currentTimeMillis(),
    val context: String,
    val source: String
)

enum class BehaviorEventType {
    MUSIC_STARTED,
    VOLUME_CHANGED,
    MODE_SWITCHED,
    COMMAND_REJECTED,
    DEVICE_CONNECTED
}
