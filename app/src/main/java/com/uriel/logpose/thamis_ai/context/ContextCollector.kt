package com.uriel.logpose.thamis_ai.context

/**
 * Aggregates information from various system providers (Bluetooth, Audio, GPS).
 */
class ContextCollector {
    fun collectAll(): Map<String, Any> {
        return mapOf(
            "is_riding" to true,
            "speed" to 45.0f
        )
    }
}
