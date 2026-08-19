package com.uriel.logpose.thamis.recovery.model

import com.uriel.logpose.thamis.monitoring.model.Anomaly
import com.uriel.logpose.thamis.monitoring.model.DiagnosticReport
import java.util.*

/**
 * Estrategias de recuperación disponibles para THAMIS.
 */
enum class RecoveryStrategy {
    RETRY,
    RESET,
    REBUILD,
    RECONNECT,
    WAIT,
    ESCALATE
}

/**
 * Estados de una decisión de recuperación.
 */
enum class RecoveryDecision {
    APPROVED,
    REJECTED,
    DEFERRED,
    EXPIRED
}

/**
 * Representa una acción atómica de recuperación.
 */
data class RecoveryAction(
    val id: String = UUID.randomUUID().toString(),
    val description: String,
    val type: String, // E.g., "CLEAR_CACHE", "REBIND_SERVICE"
    val risk: Float, // 0.0 to 1.0
    val dependencies: List<String> = emptyList(),
    val expectedResult: String
)

/**
 * Plan de recuperación estructurado.
 */
data class RecoveryPlan(
    val id: String = UUID.randomUUID().toString(),
    val targetModule: String,
    val anomaly: Anomaly,
    val diagnostic: DiagnosticReport,
    val strategy: RecoveryStrategy,
    val actions: List<RecoveryAction>,
    val priority: Int,
    val risk: Float,
    val confidence: Float,
    val timestamp: Long = System.currentTimeMillis(),
    val expirationMs: Long = 60000L
) {
    fun isExpired(): Boolean = System.currentTimeMillis() - timestamp > expirationMs
}

/**
 * Resultado de una ejecución de recuperación.
 */
data class RecoveryResult(
    val planId: String,
    val success: Boolean,
    val finalState: String,
    val error: String? = null
)
