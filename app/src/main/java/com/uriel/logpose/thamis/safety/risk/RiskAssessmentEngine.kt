package com.uriel.logpose.thamis.safety.risk

import com.uriel.logpose.thamis.safety.model.RiskLevel
import com.uriel.logpose.thamis.safety.model.RidingContext

/**
 * Evalúa el nivel de riesgo físico actual.
 */
object RiskAssessmentEngine {

    fun assess(context: RidingContext): RiskLevel {
        val speed = context.speedKmh
        val cognitiveLoad = context.estimatedCognitiveLoad

        return when {
            speed > 120f || (speed > 80f && context.isNavigationComplex) -> RiskLevel.CRITICAL
            speed > 80f || cognitiveLoad > 0.7f -> RiskLevel.HIGH
            speed > 40f || context.isCallActive -> RiskLevel.MEDIUM
            else -> RiskLevel.LOW
        }
    }
}
