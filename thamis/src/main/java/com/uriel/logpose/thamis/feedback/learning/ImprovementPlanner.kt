package com.uriel.logpose.thamis.feedback.learning

import com.uriel.logpose.thamis.feedback.model.ImprovementProposal
import com.uriel.logpose.thamis.feedback.model.ProductInsight

/**
 * Prepara propuestas de mejora basadas en insights del producto.
 */
object ImprovementPlanner {

    fun planImprovements(insights: List<ProductInsight>): List<ImprovementProposal> {
        return insights.map { insight ->
            ImprovementProposal(
                problemStatement = insight.observation,
                proposedSolution = insight.recommendation,
                priority = if (insight.impact.contains("seguridad", ignoreCase = true)) 100 else 50,
                risk = 0.3f
            )
        }
    }
}
