package com.uriel.logpose.thamis.navigation.metrics

import com.uriel.logpose.thamis.navigation.validation.NavigationValidationSession

/**
 * Calcula métricas de rendimiento para el dominio de navegación.
 */
object NavigationMetrics {

    data class Results(
        val accuracy: Float,
        val precision: Float,
        val recall: Float,
        val falsePositives: Int,
        val falseNegatives: Int,
        val confirmRate: Float,
        val rejectRate: Float,
        val averageConfidence: Float,
        val averageDecisionTimeMs: Long,
        val averageConfidenceDecay: Float
    )

    fun calculate(session: NavigationValidationSession): Results {
        val total = session.totalCommands.toFloat().coerceAtLeast(1f)
        
        return Results(
            accuracy = session.executedCommands / total,
            precision = session.executedCommands.toFloat() / (session.executedCommands + session.rejectedCommands).coerceAtLeast(1).toFloat(),
            recall = session.executedCommands.toFloat() / (session.executedCommands + session.expiredCommands).coerceAtLeast(1).toFloat(),
            falsePositives = session.rejectedCommands,
            falseNegatives = session.expiredCommands,
            confirmRate = session.confirmedCommands / total,
            rejectRate = session.rejectedCommands / total,
            averageConfidence = 0.90f, // Placeholder para cálculo real
            averageDecisionTimeMs = 45, // Placeholder
            averageConfidenceDecay = 0.15f // Placeholder
        )
    }
}
