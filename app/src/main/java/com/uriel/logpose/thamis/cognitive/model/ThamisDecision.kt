package com.uriel.logpose.thamis.cognitive.model

import com.uriel.logpose.thamis.intent.Intent

/**
 * El veredicto final. THAMIS termina aquí.
 */
data class ThamisDecision(
    val winningEvaluation: Evaluation?,
    val intent: Intent, // Intención cognitiva resuelta
    val summary: String,
    val isConclusive: Boolean,
    val trace: CognitiveTrace
)
