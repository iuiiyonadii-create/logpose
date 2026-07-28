package com.uriel.logpose.thamis.safety.engine

import com.uriel.logpose.thamis.safety.model.*

/**
 * Decide el nivel de intervención permitido basado en el riesgo y la atención.
 */
object SafetyDecisionEngine {

    fun decide(risk: RiskLevel, attention: AttentionState): SafetyAction {
        return when {
            risk == RiskLevel.CRITICAL -> SafetyAction.CANCEL
            risk == RiskLevel.HIGH || attention == AttentionState.HIGH_LOAD -> SafetyAction.SIMPLIFY
            risk == RiskLevel.MEDIUM || attention == AttentionState.BUSY -> SafetyAction.DELAY
            else -> SafetyAction.ALLOW
        }
    }
}
