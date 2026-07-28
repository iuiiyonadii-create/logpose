package com.uriel.logpose.thamis_ai.autonomy

/**
 * Prepares potential action sequences based on event prediction.
 */
class ActionPlanner {
    fun planNext(trigger: String): List<String> {
        return when (trigger) {
            "BLUETOOTH_CONNECTED" -> listOf("START_CONDUCCION", "SUGGEST_MUSIC")
            else -> emptyList()
        }
    }
}
