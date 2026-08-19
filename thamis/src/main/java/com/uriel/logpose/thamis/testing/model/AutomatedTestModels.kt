package com.uriel.logpose.thamis.testing.model

import com.uriel.logpose.thamis.lab.model.SimulationScenario

/**
 * Reporte diario de pruebas masivas.
 */
data class DailyTestReport(
    val totalTests: Int,
    val passedTests: Int,
    val failedTests: Int,
    val newErrors: List<String>,
    val regressionDetected: Boolean,
    val performanceAvgMs: Long
)

/**
 * Clasificación de la gravedad de un fallo en pruebas masivas.
 */
enum class FailureSeverity { CRITICAL, HIGH, MEDIUM, LOW }

/**
 * Resultado de una comparación de regresión.
 */
data class RegressionComparison(
    val previousLatency: Long,
    val currentLatency: Long,
    val changePct: Float,
    val module: String
)
