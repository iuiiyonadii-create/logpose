package com.uriel.logpose.thamis.cognitive.decision

import com.uriel.logpose.thamis.cognitive.model.Evidence

/**
 * Registra el motivo humano y técnico de una decisión.
 */
data class DecisionReason(
    val decisionType: String, // EXECUTE, CONFIRM, etc.
    val explanation: String,
    val supportingEvidences: List<Evidence>
)
