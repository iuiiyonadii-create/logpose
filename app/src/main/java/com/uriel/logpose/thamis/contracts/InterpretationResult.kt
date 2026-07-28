package com.uriel.logpose.thamis.contracts

import com.uriel.logpose.thamis.intent.Intent

/**
 * Primera interpretación semántica de THAMIS.
 */
data class InterpretationResult(
    val intent: Intent,
    val entities: Map<String, String> = emptyMap(),
    val normalizedText: String,
    val initialConfidence: Float
)
