package com.thamis.lab.core.contracts.decision

import com.thamis.lab.core.contracts.explanation.CognitiveExplanation

/**
 * Immutable decision output produced by THAMIS cognitive engine.
 */
public data class CognitiveDecision(
    public val intentName: String,
    public val confidenceScore: Double,
    public val actionCommand: String?,
    public val isExecutable: Boolean,
    public val explanation: CognitiveExplanation
)
