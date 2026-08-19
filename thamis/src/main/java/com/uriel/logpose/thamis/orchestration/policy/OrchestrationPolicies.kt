package com.uriel.logpose.thamis.orchestration.policy

import com.uriel.logpose.thamis.orchestration.model.OrchestrationDomain
import com.uriel.logpose.thamis.orchestration.model.PendingAction
import com.uriel.logpose.thamis.orchestration.model.RunningAction
import com.uriel.logpose.thamis.world.model.WorldSnapshot
import com.uriel.logpose.thamis.world.model.driving

/**
 * Política de prioridad dinámica basada en el estado del mundo.
 */
object PriorityPolicy {
    fun calculateEffectivePriority(action: PendingAction, worldSnapshot: WorldSnapshot): Int {
        var priority = action.priority
        
        // Ajuste por velocidad
        if (worldSnapshot.driving.speedKmh > 100) {
            if (action.domain == OrchestrationDomain.MULTIMEDIA) priority -= 200
            if (action.domain == OrchestrationDomain.NAVIGATION) priority += 100
        }

        // Ajuste por riesgo
        if (worldSnapshot.driving.riskLevel == com.uriel.logpose.thamis.world.model.RiskLevel.CRITICAL) {
            if (action.domain != OrchestrationDomain.EMERGENCY) priority = 0
        }

        return priority
    }
}

/**
 * Política de interrupciones.
 */
object InterruptPolicy {
    fun canInterrupt(newAction: PendingAction, runningAction: RunningAction): Boolean {
        // Un dominio superior siempre interrumpe a uno inferior
        if (newAction.domain == OrchestrationDomain.EMERGENCY) return true
        if (newAction.priority > (runningAction.pendingAction.priority + 100)) return true
        
        return false
    }
}

/**
 * Política de reintentos y expiración.
 */
object RetryPolicy {
    fun shouldRetry(action: PendingAction): Boolean {
        return !action.isExpired()
    }
}
