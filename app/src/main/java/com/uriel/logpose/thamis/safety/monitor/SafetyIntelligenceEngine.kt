package com.uriel.logpose.thamis.safety.monitor

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.safety.attention.AttentionEstimator
import com.uriel.logpose.thamis.safety.engine.SafetyDecisionEngine
import com.uriel.logpose.thamis.safety.model.*
import com.uriel.logpose.thamis.safety.risk.RiskAssessmentEngine

/**
 * Motor principal de inteligencia de seguridad y contexto de conducción.
 */
object SafetyIntelligenceEngine {

    fun evaluate(context: RidingContext): SafetyAssessment {
        val risk = RiskAssessmentEngine.assess(context)
        val attention = AttentionEstimator.estimate(context)
        val action = SafetyDecisionEngine.decide(risk, attention)

        LogPoseLogger.i("THAMIS_SAFETY: Riesgo: $risk | Atención: $attention | Recomendación: $action")

        return SafetyAssessment(
            risk = risk,
            reason = "Evaluación de contexto a ${context.speedKmh}km/h",
            confidence = 0.95f,
            recommendation = action
        )
    }
}
