package com.uriel.logpose.thamis.cognitive.model

/**
 * El juicio sobre una hipótesis cruzando confianza y riesgo.
 */
data class Evaluation(
    val hypothesis: Hypothesis,
    val finalScore: Float,
    val risk: Risk,
    val reasoning: String
)
