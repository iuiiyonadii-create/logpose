package com.uriel.logpose.thamis.optimization.validation

import com.uriel.logpose.thamis.optimization.model.OptimizationResult

/**
 * Valida la seguridad y estabilidad tras una acción de optimización.
 */
data class OptimizationValidationSession(
    val sessionId: String,
    val result: OptimizationResult,
    val isStable: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
