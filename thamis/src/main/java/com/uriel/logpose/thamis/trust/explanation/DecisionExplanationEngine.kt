package com.uriel.logpose.thamis.trust.explanation

import com.uriel.logpose.thamis.trust.model.DecisionExplanation

/**
 * Traduce decisiones técnicas en explicaciones comprensibles para el usuario.
 */
object DecisionExplanationEngine {

    fun explain(decisionType: String, reason: String): DecisionExplanation {
        return DecisionExplanation(
            decisionId = "DEC_${System.currentTimeMillis()}",
            summary = when (decisionType) {
                "REJECTED" -> "Decidí no actuar para proteger tu seguridad."
                "DELAYED" -> "Pospondré esto hasta que la ruta sea más tranquila."
                else -> "Acción realizada según tu petición."
            },
            detailedReason = reason,
            dataSources = listOf("WorldSnapshot", "SafetyGate"),
            confidence = 0.98f
        )
    }
}
