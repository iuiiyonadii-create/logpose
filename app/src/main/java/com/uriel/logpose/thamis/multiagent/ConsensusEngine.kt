package com.uriel.logpose.thamis.multiagent

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * FASE 27.2 — THAMIS MULTI-AGENT COLLABORATION ENGINE
 * FASE 13: CONSENSUS ENGINE
 */
object ConsensusEngine {

    /**
     * Valida una decisión mediante la revisión de múltiples agentes.
     * Retorna TRUE si se alcanza el quórum.
     */
    fun reachConsensus(proposal: String, reviewers: List<String>): Boolean {
        LogPoseLogger.i("ConsensusEngine: Iniciando ronda de revisión para: $proposal")
        
        var approvals = 0
        reviewers.forEach { reviewerName ->
            val agent = AgentRegistry.getAgent(reviewerName)
            if (agent != null) {
                if (agent.vote(proposal)) {
                    approvals++
                    LogPoseLogger.d("ConsensusEngine: Agente $reviewerName APRUEBA.")
                } else {
                    LogPoseLogger.e("ConsensusEngine: Agente $reviewerName RECHAZA propuesta.")
                }
            }
        }
        
        // SINCRO CLAUDE: Umbral de consenso del 60% según especificación THAMIS LAB
        val approvalRate = approvals.toFloat() / reviewers.size.coerceAtLeast(1)
        val reached = approvalRate >= 0.60f
        
        if (reached) {
            LogPoseLogger.i("ConsensusEngine: CONSENSO ALCANZADO (${(approvalRate * 100).toInt()}%).")
        } else {
            LogPoseLogger.w("ConsensusEngine: FALLO DE CONSENSO (${(approvalRate * 100).toInt()}%). Se requiere mediación.")
        }
        return reached
    }
}
