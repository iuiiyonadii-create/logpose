package com.uriel.logpose.thamis.cognitive.model

/**
 * La Caja Negra de THAMIS.
 * Permite la auditoría completa de un ciclo de pensamiento.
 */
data class CognitiveTrace(
    val id: String,
    val engineVersion: String,
    val startTime: Long,
    val endTime: Long,
    val durationMs: Long,
    val steps: List<String>,
    val hypotheses: List<Hypothesis>,
    val evaluations: List<Evaluation>,
    val evidencesEvaluated: Int = 0,
    val rulesApplied: List<String> = emptyList()
)
