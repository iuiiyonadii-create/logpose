package com.uriel.logpose.thamis.actuation

import com.thamis.lab.core.contracts.intent.Intent

/**
 * Representa el resultado de un intento de actuación de THAMIS.
 */
data class ActuationResult(
    val success: Boolean,
    val action: Intent,
    val reason: String,
    val cognitiveTraceId: String,
    val timestamp: Long = System.currentTimeMillis()
)
