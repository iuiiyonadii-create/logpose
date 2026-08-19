package com.uriel.logpose.thamis.planning.model

import java.util.*

/**
 * Niveles de prioridad para la planificación cognitiva THAMIS.
 */
enum class PlanningPriority {
    CRITICAL,
    HIGH,
    NORMAL,
    LOW,
    BACKGROUND
}

/**
 * Estrategias de planificación según el contexto del mundo.
 */
enum class PlanningStrategy {
    FASTEST,
    SAFEST,
    LOWEST_RISK,
    LOWEST_INTERRUPTION,
    USER_PRIORITY,
    ENERGY_SAVING
}

/**
 * Decisiones del Scheduler de planificación.
 */
enum class PlanningDecision {
    EXECUTE_NOW,
    WAIT,
    POSTPONE,
    RETRY,
    CANCEL,
    IGNORE
}

/**
 * Estado de un paso individual del plan.
 */
enum class StepStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    FAILED,
    SKIPPED
}

/**
 * Representa un paso atómico de ejecución dentro de un plan.
 */
data class PlanningStep(
    val id: String = UUID.randomUUID().toString(),
    val description: String,
    val dependencies: List<String> = emptyList(),
    var status: StepStatus = StepStatus.PENDING,
    val expectedResult: String,
    val estimatedTimeMs: Long = 0
)

/**
 * Representa un plan de acción de reversión si el plan principal falla.
 */
data class RollbackPlan(
    val id: String = UUID.randomUUID().toString(),
    val steps: List<PlanningStep>,
    val reason: String
)

/**
 * El modelo maestro de un Plan de Ejecución Cognitivo.
 */
data class ExecutionPlan(
    val id: String = UUID.randomUUID().toString(),
    val goal: String,
    val priority: PlanningPriority,
    val risk: Float,
    val confidence: Float,
    val strategy: PlanningStrategy,
    val estimatedDurationMs: Long,
    val conditions: List<String>,
    val dependencies: List<String>,
    val rollback: RollbackPlan?,
    val timeoutMs: Long = 30000,
    val creationTime: Long = System.currentTimeMillis(),
    val expirationTime: Long = creationTime + 60000
) {
    fun isExpired(): Boolean = System.currentTimeMillis() > expirationTime
}

/**
 * Resultado de la ejecución de un plan.
 */
data class PlanningResult(
    val planId: String,
    val success: Boolean,
    val finalState: String,
    val executionTimeMs: Long
)
