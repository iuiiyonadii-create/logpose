package com.uriel.logpose.thamis.monitoring.model

import java.util.*

/**
 * Estado general de salud del cerebro THAMIS.
 */
enum class HealthState {
    EXCELLENT, GOOD, WARNING, CRITICAL, OFFLINE
}

/**
 * Representa la salud de un dominio específico.
 */
data class DomainHealth(
    val domainName: String,
    val availability: Float, // 0.0 to 1.0
    val latencyMs: Long,
    val confidence: Float,
    val errorRate: Float,
    val stabilityScore: Float,
    val healthScore: Int // 0 to 100
)

/**
 * Reporte consolidado de salud del sistema.
 */
data class BrainHealth(
    val globalScore: Int,
    val state: HealthState,
    val domains: Map<String, DomainHealth>,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Captura de rendimiento en un instante dado.
 */
data class PerformanceSnapshot(
    val module: String,
    val executionTimeMs: Long,
    val memoryUsageKb: Long = 0,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Detección de un comportamiento inusual o erróneo.
 */
data class Anomaly(
    val id: String = UUID.randomUUID().toString(),
    val module: String,
    val type: AnomalyType,
    val severity: Severity,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class AnomalyType {
    HIGH_LATENCY, EXCESSIVE_RETRIES, INCONSISTENT_CONTEXT, DECREASING_CONFIDENCE,
    DUPLICATE_EVENTS, INVALID_SNAPSHOT, BLOCKED_QUEUE, REPETITIVE_PLANNING, TIMEOUT
}

enum class Severity { LOW, MEDIUM, HIGH, CRITICAL }

/**
 * Sugerencia de recuperación generada por el motor.
 */
data class RecoverySuggestion(
    val id: String = UUID.randomUUID().toString(),
    val targetModule: String,
    val action: String,
    val reason: String,
    val expectedImpact: String
)

/**
 * Informe de diagnóstico detallado.
 */
data class DiagnosticReport(
    val summary: String,
    val issuesFound: List<String>,
    val recommendations: List<RecoverySuggestion>,
    val timestamp: Long = System.currentTimeMillis()
)
