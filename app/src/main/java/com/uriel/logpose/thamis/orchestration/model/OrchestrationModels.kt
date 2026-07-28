package com.uriel.logpose.thamis.orchestration.model

import java.util.*

/**
 * Representa una acción que THAMIS ha decidido realizar pero aún no ha orquestado.
 */
data class PendingAction(
    val id: String = UUID.randomUUID().toString(),
    val domain: OrchestrationDomain,
    val priority: Int,
    val intent: String,
    val payload: Any,
    val timestamp: Long = System.currentTimeMillis(),
    val expiryMs: Long = 10_000L
) {
    fun isExpired(): Boolean = System.currentTimeMillis() - timestamp > expiryMs
}

/**
 * Representa una acción que se encuentra actualmente en ejecución.
 */
data class RunningAction(
    val pendingAction: PendingAction,
    val startTime: Long = System.currentTimeMillis()
)

/**
 * Representa una acción que ha sido bloqueada por un conflicto de prioridad.
 */
data class BlockedAction(
    val pendingAction: PendingAction,
    val blockedByActionId: String,
    val reason: String
)

/**
 * Dominios que el orquestador coordina.
 */
enum class OrchestrationDomain {
    SAFETY,
    NAVIGATION,
    COMMUNICATION,
    MULTIMEDIA,
    SYSTEM,
    INFORMATION,
    EMERGENCY
}

/**
 * Decisiones del orquestador.
 */
enum class OrchestrationDecision {
    EXECUTE_NOW,
    WAIT,
    CANCEL,
    RETRY,
    IGNORE
}

/**
 * Estado general de la orquestación.
 */
data class OrchestrationState(
    val activeActions: List<RunningAction> = emptyList(),
    val queuedActions: List<PendingAction> = emptyList(),
    val blockedActions: List<BlockedAction> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)
