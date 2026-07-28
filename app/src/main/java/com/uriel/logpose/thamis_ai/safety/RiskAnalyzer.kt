package com.uriel.logpose.thamis_ai.safety

import com.uriel.logpose.domain.models.DrivingState

/**
 * Advanced risk evaluator for driving scenarios.
 */
class RiskAnalyzer {

    fun evaluate(context: Map<String, Any>): SafetyLevel {
        val drivingState = context["driving_state"] as? DrivingState ?: DrivingState.IDLE
        val speed = context["speed"] as? Float ?: 0.0f
        
        return when {
            speed > 100 -> SafetyLevel.CRITICAL
            drivingState == DrivingState.RIDING && speed > 60 -> SafetyLevel.HIGH
            drivingState == DrivingState.RIDING -> SafetyLevel.MEDIUM
            else -> SafetyLevel.SAFE
        }
    }
}
