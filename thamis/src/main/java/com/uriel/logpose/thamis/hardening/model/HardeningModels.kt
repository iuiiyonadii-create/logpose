package com.uriel.logpose.thamis.hardening.model

import java.util.*

/**
 * Representa el estado de estabilidad de un módulo o del sistema.
 */
data class StabilityState(
    val module: String,
    val healthy: Boolean,
    val failureCount: Int,
    val lastRecoverySuccessful: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Informe de detección de regresiones de rendimiento o calidad.
 */
data class RegressionReport(
    val module: String,
    val metric: String,
    val previousValue: Double,
    val currentValue: Double,
    val deviationPct: Double,
    val isRegression: Boolean
)

/**
 * Análisis de un fallo específico.
 */
data class FailureReport(
    val id: String = UUID.randomUUID().toString(),
    val severity: FailureSeverity,
    val module: String,
    val cause: String,
    val impact: String,
    val recommendation: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class FailureSeverity { CRITICAL, HIGH, MEDIUM, LOW }

/**
 * Definición de objetivos de rendimiento (Baseline).
 */
data class PerformanceTarget(
    val operation: String,
    val targetMs: Long,
    val criticalLimitMs: Long,
    val memoryLimitKb: Long
)

/**
 * Estadísticas de fiabilidad del sistema.
 */
data class ReliabilityStats(
    val uptimeMs: Long,
    val availabilityPct: Float,
    val meanTimeToFailureMs: Long,
    val meanTimeToRecoveryMs: Long
)
