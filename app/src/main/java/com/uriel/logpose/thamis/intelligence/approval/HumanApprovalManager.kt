package com.uriel.logpose.thamis.intelligence.approval

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * FASE 27.0 — THAMIS AUTONOMOUS ENGINEERING CORE
 * FASE 16: HUMAN APPROVAL SYSTEM
 *
 * Regla absoluta: THAMIS propone, el humano decide.
 */
object HumanApprovalManager {

    enum class ApprovalStatus { PROPOSED, REVIEWING, APPROVED, REJECTED, MODIFIED }

    private var currentStatus = ApprovalStatus.PROPOSED

    fun requestApproval(proposalId: String) {
        currentStatus = ApprovalStatus.REVIEWING
        LogPoseLogger.i("HumanApprovalManager: Solicitando aprobación para $proposalId")
    }

    fun approve() {
        currentStatus = ApprovalStatus.APPROVED
        LogPoseLogger.i("HumanApprovalManager: Propuesta APROBADA.")
    }

    fun getStatus(): ApprovalStatus = currentStatus
}
