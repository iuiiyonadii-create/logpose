package com.uriel.logpose.thamis.feedback.trace

import com.uriel.logpose.thamis.feedback.model.FeedbackCategory

/**
 * Registro auditable de un ciclo de aprendizaje por feedback.
 */
data class FeedbackTrace(
    val eventId: String,
    val category: FeedbackCategory,
    val analysisResult: String,
    val insightGenerated: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

object FeedbackAudit {
    private val logs = mutableListOf<FeedbackTrace>()

    fun record(trace: FeedbackTrace) {
        logs.add(trace)
        if (logs.size > 500) logs.removeAt(0)
    }

    fun getLogs(): List<FeedbackTrace> = logs.toList()
}
