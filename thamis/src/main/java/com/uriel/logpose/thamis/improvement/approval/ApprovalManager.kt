package com.uriel.logpose.thamis.improvement.approval

import com.uriel.logpose.thamis.improvement.model.ImprovementProposal
import com.uriel.logpose.thamis.improvement.model.ProposalStatus
import com.uriel.logpose.thamis.improvement.pipeline.ImprovementPipelineEngine

/**
 * Gestiona los estados de aprobación de las mejoras.
 */
object ApprovalManager {

    enum class ApprovalState { PENDING, APPROVED, REJECTED, DEFERRED }

    fun approve(proposal: ImprovementProposal) {
        ImprovementPipelineEngine.advanceStatus(proposal, ProposalStatus.APPROVED)
    }

    fun reject(proposal: ImprovementProposal) {
        ImprovementPipelineEngine.advanceStatus(proposal, ProposalStatus.REJECTED)
    }
}
