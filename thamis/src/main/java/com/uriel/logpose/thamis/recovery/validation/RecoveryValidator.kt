package com.uriel.logpose.thamis.recovery.validation

import com.uriel.logpose.thamis.recovery.model.RecoveryPlan
import com.uriel.logpose.thamis.world.model.WorldSnapshot

/**
 * Validador de coherencia y seguridad para planes de recuperación.
 */
object RecoveryValidator {

    fun validate(plan: RecoveryPlan, world: WorldSnapshot): List<String> {
        val issues = mutableListOf<String>()

        if (plan.risk > 0.8f && world.vehicle.speedKmh > 80) {
            issues.add("Riesgo excesivo para la velocidad actual")
        }

        if (plan.actions.isEmpty()) {
            issues.add("El plan no contiene acciones")
        }

        return issues
    }
}
