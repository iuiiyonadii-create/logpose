package com.uriel.logpose.thamis.feedback.model

import java.util.*

/**
 * Categorías de feedback para clasificar la experiencia del usuario.
 */
enum class FeedbackCategory {
    BUG, VOICE, UX, SAFETY, PERFORMANCE, CONFUSION, REQUEST
}

/**
 * Representa un evento de feedback individual.
 */
data class UserFeedbackEvent(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val userId: String,
    val contextSnapshotId: String?,
    val rawFeedback: String,
    val category: FeedbackCategory,
    val priority: Int // 0 to 100
)

/**
 * Conclusión técnica extraída del análisis de feedback.
 */
data class ProductInsight(
    val id: String = UUID.randomUUID().toString(),
    val observation: String,
    val impact: String,
    val frequency: Int,
    val recommendation: String
)

/**
 * Propuesta formal de mejora derivada de un insight.
 */
data class ImprovementProposal(
    val id: String = UUID.randomUUID().toString(),
    val problemStatement: String,
    val proposedSolution: String,
    val priority: Int,
    val risk: Float // 0.0 to 1.0
)
