package com.uriel.logpose.thamis.planning.rollback

import com.uriel.logpose.thamis.planning.model.ExecutionPlan
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Motor de reversión de estados.
 */
object RollbackEngine {

    fun trigger(plan: ExecutionPlan) {
        val rollback = plan.rollback ?: return
        LogPoseLogger.w("[THAMIS_ROLLBACK] Triggering rollback for plan: ${plan.id}. Reason: ${rollback.reason}")
        
        rollback.steps.forEach { step ->
            LogPoseLogger.d("[THAMIS_ROLLBACK] Executing step: ${step.description}")
        }
    }
}
