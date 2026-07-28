package com.uriel.logpose.thamis.shadow

import com.uriel.logpose.thamis.cognitive.model.ThamisDecision
import com.uriel.logpose.thamis.cognitive.model.CognitiveTrace
import com.uriel.logpose.thamis.intent.Intent

/**
 * Representa la comparación entre THAMIS v3.0 y el sistema legado.
 */
data class ShadowResult(
    val input: String,
    val thamisDecision: ThamisDecision,
    val legacyIntent: Intent,
    val isMatch: Boolean,
    val processingTimeMs: Long,
    val trace: CognitiveTrace
)
