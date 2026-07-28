package com.uriel.logpose.thamis.improvement.pipeline

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.improvement.model.ImprovementProposal
import com.uriel.logpose.thamis.improvement.model.ProposalStatus

/**
 * Orquestador central del ciclo de evolución de THAMIS.
 */
object ImprovementPipelineEngine {
    private val proposals = mutableListOf<ImprovementProposal>()

    fun submitRequest(proposal: ImprovementProposal) {
        proposals.add(proposal)
        LogPoseLogger.i("THAMIS_IMPROVEMENT: Nueva propuesta recibida: ${proposal.title}")
        advanceStatus(proposal, ProposalStatus.ANALYZING)
    }

    fun advanceStatus(proposal: ImprovementProposal, newStatus: ProposalStatus) {
        proposal.status = newStatus
        LogPoseLogger.d("THAMIS_PIPELINE: Propuesta ${proposal.id} movida a $newStatus")
    }

    fun getProposals(): List<ImprovementProposal> = proposals.toList()
}
