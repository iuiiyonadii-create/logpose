package com.uriel.logpose.thamis.cognitive.model

/**
 * Representa una interpretación potencial del deseo del usuario.
 */
data class Hypothesis(
    val candidateGoal: Goal,
    val entities: Map<String, String>,
    val evidences: List<Evidence>,
    val rawConfidence: Float // Puntuación inicial antes de evaluar el riesgo
)
