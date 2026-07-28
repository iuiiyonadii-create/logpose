package com.uriel.logpose.thamis.orchestration.scheduler

import com.uriel.logpose.thamis.orchestration.model.*
import com.uriel.logpose.thamis.orchestration.policy.*
import com.uriel.logpose.thamis.world.model.WorldSnapshot

/**
 * Planificador de acciones individuales.
 */
object ActionScheduler {

    fun schedule(
        action: PendingAction, 
        activeActions: List<RunningAction>, 
        worldSnapshot: WorldSnapshot
    ): OrchestrationDecision {
        
        // 1. Verificar expiración
        if (action.isExpired()) return OrchestrationDecision.IGNORE

        // 2. Calcular prioridad efectiva
        val effectivePriority = PriorityPolicy.calculateEffectivePriority(action, worldSnapshot)
        if (effectivePriority <= 0) return OrchestrationDecision.IGNORE

        // 3. Evaluar conflictos con acciones en ejecución
        for (running in activeActions) {
            if (InterruptPolicy.canInterrupt(action, running)) {
                return OrchestrationDecision.EXECUTE_NOW
            } else {
                return OrchestrationDecision.WAIT
            }
        }

        // 4. Si no hay nada corriendo, ejecutar ya
        return OrchestrationDecision.EXECUTE_NOW
    }
}
