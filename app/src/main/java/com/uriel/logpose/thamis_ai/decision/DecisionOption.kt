package com.uriel.logpose.thamis_ai.decision

/**
 * Representation of a potential action to take.
 */
data class DecisionOption(
    val type: String,
    val riskScore: Float,
    val utilityScore: Float
)
