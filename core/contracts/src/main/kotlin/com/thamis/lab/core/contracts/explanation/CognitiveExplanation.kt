package com.thamis.lab.core.contracts.explanation

/**
 * Audit trail explaining the reasoning process behind a CognitiveDecision.
 */
public data class CognitiveExplanation(
    public val reasoningTrace: String,
    public val evidencesUsed: List<String> = emptyList(),
    public val evaluatedPolicies: List<String> = emptyList(),
    public val finalConfidence: Double
)
