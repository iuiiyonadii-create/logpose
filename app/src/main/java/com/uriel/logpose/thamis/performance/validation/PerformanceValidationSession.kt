package com.uriel.logpose.thamis.performance.validation

import com.uriel.logpose.thamis.performance.model.PerformanceReport

/**
 * Sesión de validación de los datos recolectados por el Profiler.
 */
data class PerformanceValidationSession(
    val sessionId: String,
    val report: PerformanceReport,
    val inconsistencies: List<String>,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun isReliable(): Boolean = inconsistencies.isEmpty()
}
