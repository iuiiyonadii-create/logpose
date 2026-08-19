package com.uriel.logpose.thamis.trust.monitor

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.trust.explanation.DecisionExplanationEngine
import com.uriel.logpose.thamis.trust.model.DecisionExplanation

/**
 * Motor central de confianza y transparencia v1.0.
 */
object TrustEngine {

    fun requestExplanation(decisionType: String, technicalReason: String): DecisionExplanation {
        LogPoseLogger.i("THAMIS_TRUST: Generando explicación para decisión: $decisionType")
        
        val explanation = DecisionExplanationEngine.explain(decisionType, technicalReason)
        
        LogPoseLogger.d("THAMIS_EXPLANATION: ${explanation.summary}")
        
        return explanation
    }
}
