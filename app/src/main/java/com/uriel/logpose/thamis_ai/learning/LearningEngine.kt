package com.uriel.logpose.thamis_ai.learning

import com.uriel.logpose.domain.learning.Recommendation
import java.util.UUID

/**
 * Core engine for processing behavioral events and updating models.
 */
class LearningEngine {

    private val analyzer = PatternAnalyzer()
    private val confidenceEvaluator = ConfidenceEvaluator()

    fun processEvent(event: BehaviorEvent): Recommendation? {
        val pattern = analyzer.analyze(event) ?: return null
        val confidence = confidenceEvaluator.evaluate(pattern.frequency)

        return if (confidence >= 0.7f) {
            Recommendation(
                id = UUID.randomUUID().toString(),
                text = "Noté que solés usar ${pattern.action}. ¿Querés activarlo automáticamente?",
                action = pattern.action
            )
        } else null
    }
}
