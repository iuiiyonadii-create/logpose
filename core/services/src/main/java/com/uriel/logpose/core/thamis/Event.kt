package com.uriel.logpose.core.thamis

import java.util.UUID

/**
 * Global event model for THAMIS Core communication.
 */
data class Event(
    val id: String = UUID.randomUUID().toString(),
    val type: EventType,
    val source: String,
    val timestamp: Long = System.currentTimeMillis(),
    val data: Any? = null
)

enum class EventType {
    VOICE_COMMAND,
    DEVICE_CONNECTED,
    DEVICE_DISCONNECTED,
    MESSAGE_RECEIVED,
    NAVIGATION_UPDATE,
    CALL_STATUS_CHANGED,
    SYSTEM_ALERT
}
