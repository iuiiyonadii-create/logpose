package com.uriel.logpose.thamis.recovery.rollback

import com.uriel.logpose.thamis.recovery.model.RecoveryPlan
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Motor de reversión de recuperaciones fallidas o canceladas.
 */
object RecoveryRollbackEngine {

    fun prepareRollback(plan: ExecutionPlan): RecoveryPlan {
        // En v1.0, solo registramos la intención de rollback
        LogPoseLogger.w("[THAMIS_ROLLBACK] Preparando reversión para el plan: ${plan.id}")
        
        // Simular creación de un plan inverso (placeholder)
        return plan.copy(
            strategy = com.uriel.logpose.thamis.recovery.model.RecoveryStrategy.WAIT,
            priority = 1000 // Máxima prioridad para limpiar el sistema
        )
    }
}

// Alias de conveniencia para no romper si se usa ExecutionPlan en lugar de RecoveryPlan
typealias ExecutionPlan = com.uriel.logpose.thamis.recovery.model.RecoveryPlan
