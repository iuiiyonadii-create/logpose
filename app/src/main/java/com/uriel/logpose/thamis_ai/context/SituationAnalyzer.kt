package com.uriel.logpose.thamis_ai.context

/**
 * Transforms raw context data into high-level situational understanding.
 */
class SituationAnalyzer {
    fun analyze(data: Map<String, Any>): String {
        val isRiding = data["is_riding"] as? Boolean ?: false
        return if (isRiding) "Rider is actively driving" else "Rider is stationary"
    }
}
