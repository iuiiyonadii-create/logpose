package com.uriel.logpose.thamis.feedback.analysis

import com.uriel.logpose.thamis.feedback.model.FeedbackCategory
import com.uriel.logpose.thamis.feedback.model.UserFeedbackEvent

/**
 * Analiza el impacto y frecuencia del feedback recibido.
 */
object FeedbackAnalyzer {

    fun analyze(events: List<UserFeedbackEvent>): Map<String, Int> {
        val statistics = mutableMapOf<String, Int>()
        
        events.forEach { event ->
            val key = event.category.name
            statistics[key] = (statistics[key] ?: 0) + 1
        }

        return statistics
    }

    fun calculateSeverity(event: UserFeedbackEvent): Int {
        var score = event.priority
        if (event.category == FeedbackCategory.SAFETY) score += 50
        if (event.category == FeedbackCategory.BUG) score += 30
        
        return score.coerceIn(0, 100)
    }
}
