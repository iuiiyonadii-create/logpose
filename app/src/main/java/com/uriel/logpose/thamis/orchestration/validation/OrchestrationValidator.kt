package com.uriel.logpose.thamis.orchestration.validation

import com.uriel.logpose.thamis.orchestration.model.OrchestrationState

/**
 * Validador de consistencia del estado de orquestación.
 */
object OrchestrationValidator {

    fun validate(state: OrchestrationState): List<String> {
        val issues = mutableListOf<String>()

        // 1. Detectar deadlocks (simplificado: demasiadas acciones en cola sin movimiento)
        if (state.queuedActions.size > 10) {
            issues.add("Posible hambruna de eventos: más de 10 acciones en cola")
        }

        // 2. Verificar prioridades
        state.activeActions.forEach { running ->
            val higherPriorityInQueue = state.queuedActions.any { it.priority > (running.pendingAction.priority + 200) }
            if (higherPriorityInQueue) {
                issues.add("Inconsistencia: Existe una acción con prioridad mucho mayor en cola que la actual")
            }
        }

        return issues
    }
}
