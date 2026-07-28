package com.uriel.logpose.thamis.improvement.model

import java.util.*

/**
 * Solicitud de mejora técnica.
 */
data class ImprovementRequest(
    val id: String = UUID.randomUUID().toString(),
    val sourceId: String, // Insight ID o Feedback ID
    val problemDescription: String,
    val evidence: String,
    val priority: Int
)

/**
 * Propuesta formal de mejora.
 */
data class ImprovementProposal(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val objective: String,
    val affectedModules: List<String>,
    val riskScore: Float,
    val expectedBenefit: String,
    var status: ProposalStatus = ProposalStatus.RECEIVED
)

enum class ProposalStatus {
    RECEIVED, ANALYZING, PROPOSED, VALIDATING, APPROVED, REJECTED, IMPLEMENTED, ARCHIVED
}

/**
 * Análisis de impacto de un cambio propuesto.
 */
data class ImpactAnalysis(
    val proposalId: String,
    val technicalImpact: String,
    val userImpact: String,
    val regressionRisk: Float,
    val compatibilityCheck: Boolean
)

/**
 * Resultado de un experimento controlado.
 */
data class ExperimentResult(
    val hypothesis: String,
    val testCase: String,
    val metrics: Map<String, Any>,
    val conclusion: String,
    val isSuccessful: Boolean
)
