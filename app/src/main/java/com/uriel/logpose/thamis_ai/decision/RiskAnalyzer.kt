package com.uriel.logpose.thamis_ai.decision

import com.uriel.logpose.domain.models.DrivingState

/**
 * Analyzes risk levels for proposed actions.
 */
class RiskAnalyzer {

    fun analyze(state: DrivingState, action: String): Float {
        return if (state == DrivingState.RIDING && action == "READ_FULL_MESSAGE") {
            0.9f // High risk
        } else {
            0.1f // Low risk
        }
    }
}
