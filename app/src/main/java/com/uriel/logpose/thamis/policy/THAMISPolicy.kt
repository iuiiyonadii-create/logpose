package com.uriel.logpose.thamis.policy

import com.uriel.logpose.thamis.decision.Decision

/**
 * Define políticas de ejecución para las decisiones de THAMIS.
 */
object THAMISPolicy {

    fun evaluate(decision: Decision): Decision {
        // Por ejemplo, si la confianza es muy baja, forzamos que sea UNKNOWN
        if (decision.confidence < 0.4f) {
            return decision.copy(
                intent = com.uriel.logpose.thamis.intent.Intent.UNKNOWN,
                requiresConfirmation = false
            )
        }
        
        return decision
    }
}