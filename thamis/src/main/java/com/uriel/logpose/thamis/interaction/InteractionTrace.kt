package com.uriel.logpose.thamis.interaction

/**
 * Registro histórico de una planificación.
 */
data class InteractionTrace(
    val requestId: String,
    val domain: InteractionRequest.Domain,
    val decision: InteractionDecision,
    val priority: Int,
    val competingEvents: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val reason: String
)

object InteractionMetrics {
    var totalRequests = 0
    var executedCount = 0
    var ignoredCount = 0
    var mergedCount = 0
    var totalLatencyMs = 0L

    fun record(decision: InteractionDecision) {
        totalRequests++
        when (decision) {
            InteractionDecision.EXECUTE -> executedCount++
            InteractionDecision.IGNORE -> ignoredCount++
            InteractionDecision.MERGE -> mergedCount++
            else -> {}
        }
    }
}
