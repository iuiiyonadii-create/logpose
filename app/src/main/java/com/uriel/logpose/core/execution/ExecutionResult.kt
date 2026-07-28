package com.uriel.logpose.core.execution

/**
 * Resultado de una ejecución.
 */
data class ExecutionResult(
    val success: Boolean,
    val message: String = ""
)
