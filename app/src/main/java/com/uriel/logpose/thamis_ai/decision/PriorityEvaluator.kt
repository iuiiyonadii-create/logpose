package com.uriel.logpose.thamis_ai.decision

/**
 * Calculates priority based on event type and context.
 */
class PriorityEvaluator {

    fun getLevel(eventType: String): Int {
        return when (eventType) {
            "SAFETY_ALERT" -> 1
            "NAVIGATION" -> 2
            "CALL" -> 3
            "MESSAGE" -> 4
            else -> 5
        }
    }
}
