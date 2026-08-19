package com.uriel.logpose.thamis.recovery.policy

import com.uriel.logpose.thamis.recovery.model.RecoveryDecision
import com.uriel.logpose.thamis.recovery.model.RecoveryPlan
import com.uriel.logpose.thamis.recovery.model.RecoveryStrategy
import com.uriel.logpose.thamis.world.model.WorldSnapshot

/**
 * Motor de políticas de recuperación. Decide si un plan es seguro para ser aprobado.
 */
object RecoveryPolicyEngine {

    fun evaluate(plan: RecoveryPlan, world: WorldSnapshot): RecoveryDecision {
        val speed = world.vehicle.speedKmh
        val risk = world.vehicle.riskLevel.name

        // Regla: No reiniciar componentes críticos a alta velocidad
        if (speed > 100f && plan.risk > 0.5f) {
            return RecoveryDecision.REJECTED
        }

        // Regla: Riesgo crítico bloquea recuperaciones complejas
        if (risk == "CRITICAL" && plan.strategy != RecoveryStrategy.WAIT) {
            return RecoveryDecision.REJECTED
        }

        // Regla: Expiración
        if (plan.isExpired()) {
            return RecoveryDecision.EXPIRED
        }

        return RecoveryDecision.APPROVED
    }
}
