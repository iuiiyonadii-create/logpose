package com.uriel.logpose.thamis_ai.proactive

import android.util.Log

/**
 * High-level coordinator for anticipatory assistance.
 */
class ProactiveAssistant {

    private val detector = OpportunityDetector()
    private val engine = SuggestionEngine()

    fun analyzeSituation(context: Map<String, Any>): String? {
        val opportunity = detector.detect(context) ?: return null
        val suggestion = engine.generate(opportunity)
        Log.d("Proactive", "Created suggestion: $suggestion")
        return suggestion
    }
}
