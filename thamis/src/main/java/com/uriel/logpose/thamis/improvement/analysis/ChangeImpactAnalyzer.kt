package com.uriel.logpose.thamis.improvement.analysis

import com.uriel.logpose.thamis.improvement.model.ImpactAnalysis
import com.uriel.logpose.thamis.improvement.model.ImprovementProposal

/**
 * Evalúa el riesgo y alcance de un cambio propuesto.
 */
object ChangeImpactAnalyzer {

    fun analyze(proposal: ImprovementProposal): ImpactAnalysis {
        val modulesCount = proposal.affectedModules.size
        val risk = (modulesCount * 0.2f).coerceAtMost(1.0f)
        
        return ImpactAnalysis(
            proposalId = proposal.id,
            technicalImpact = if (risk > 0.5f) "HIGH_COMPLEXITY" else "MODERATE",
            userImpact = "UX_EVOLUTION",
            regressionRisk = risk,
            compatibilityCheck = true
        )
    }
}
