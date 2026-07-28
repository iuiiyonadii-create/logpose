package com.uriel.logpose.thamis_ai.learning

import com.uriel.logpose.domain.learning.BehaviorPattern

/**
 * Logic for detecting repetitions in user behavior.
 */
class PatternAnalyzer {

    private val history = mutableListOf<BehaviorEvent>()

    fun analyze(event: BehaviorEvent): BehaviorPattern? {
        history.add(event)
        
        // Example: count how many times this event happened in the last 10 actions
        val count = history.filter { it.eventType == event.eventType }.size
        
        return if (count >= 5) {
            BehaviorPattern(
                action = event.eventType.name,
                frequency = count,
                triggers = listOf(event.context)
            )
        } else null
    }
}
