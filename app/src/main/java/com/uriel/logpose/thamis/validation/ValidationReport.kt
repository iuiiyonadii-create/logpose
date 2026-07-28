package com.uriel.logpose.thamis.validation

/**
 * Informe consolidado de una sesión de validación real.
 */
data class ValidationReport(
    val engineVersion: String,
    val totalSamples: Int,
    val totalAccuracy: Float,
    val matchCount: Int,
    val positiveDivergences: Int, // THAMIS fue mejor
    val negativeDivergences: Int, // THAMIS fue peor
    val criticalErrors: Int,
    val memoryUsageRate: Float = 0f,
    val averageFinalConfidence: Float = 0f,
    val expiredIntentsCount: Int = 0,
    val navigationProviderSuccessRate: Float = 1.0f,
    val averageNavigationLatencyMs: Long = 0,
    val authorityFallbacksCount: Int = 0,
    val recommendations: List<String>
)
