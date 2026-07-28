package com.uriel.logpose.thamis_ai.decision

/**
 * Result of the THAMIS decision process.
 */
data class DecisionResult(
    val action: DecisionAction,
    val confidence: Float,
    val reason: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class DecisionAction {
    EXECUTE,
    WAIT,
    IGNORE,
    ASK_CONFIRMATION
}
