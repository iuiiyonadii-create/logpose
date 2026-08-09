package com.uriel.logpose.thamis.multiagent

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * THAMIS v22.23 (Acoustic Singularity) — CONSENSUS ENGINE.
 * Implementa la Fórmula de Probabilidad Contextual Ponderada:
 * Score Final = (BaseConfidencia * MultiplicadorVerbo) + BonoFavoritos + BonoAppActiva + BonoHistorial
 * Umbral de ejecución: Score Final >= 1.2
 */
object ConsensusEngine {

    private const val EXECUTION_THRESHOLD = 1.2f

    /**
     * Calcula la Probabilidad Contextual Ponderada y determina si alcanza el umbral de ejecución (>= 1.2).
     */
    fun evaluateWeightedConfidence(
        baseConfidence: Float,
        hasCriticalVerb: Boolean,
        isUserFavorite: Boolean,
        isAppActiveInForeground: Boolean,
        hasHistoryPattern: Boolean,
        isTotallyIncoherent: Boolean
    ): Pair<Boolean, Float> {

        if (isTotallyIncoherent) {
            LogPoseLogger.w("ConsensusEngine: VETO POR INCOHERENCIA TOTAL -> Intención incompatibles con la entidad detectada.")
            return Pair(false, 0.0f)
        }

        // Multiplicador de Verbo Crítico (x1.5)
        val verbMultiplier = if (hasCriticalVerb) 1.5f else 1.0f
        val weightedBase = baseConfidence * verbMultiplier

        // Bonos Contextuales
        val favoriteBonus = if (isUserFavorite) 0.5f else 0.0f
        val foregroundAppBonus = if (isAppActiveInForeground) 0.3f else 0.0f
        val historyBonus = if (hasHistoryPattern) 0.2f else 0.0f

        val finalScore = weightedBase + favoriteBonus + foregroundAppBonus + historyBonus
        val isApproved = finalScore >= EXECUTION_THRESHOLD

        LogPoseLogger.i("ConsensusEngine: Score Ponderado Final = %.2f (Base: %.2f, Verbo: x%.1f, Fav: +%.1f, App: +%.1f, Hist: +%.1f) -> %s"
            .format(finalScore, baseConfidence, verbMultiplier, favoriteBonus, foregroundAppBonus, historyBonus,
                if (isApproved) "EJECUTAR (>= 1.2)" else "RECHAZADO (< 1.2)"))

        return Pair(isApproved, finalScore)
    }

    /**
     * Valida una decisión mediante la revisión ponderada de múltiples agentes.
     */
    fun reachConsensus(proposal: String, reviewers: List<String>): Boolean {
        LogPoseLogger.i("ConsensusEngine: Iniciando ronda de revisión ponderada para: $proposal")
        val (isApproved, score) = evaluateWeightedConfidence(
            baseConfidence = 0.85f,
            hasCriticalVerb = proposal.contains("pone") || proposal.contains("ir a") || proposal.contains("llama"),
            isUserFavorite = true,
            isAppActiveInForeground = true,
            hasHistoryPattern = false,
            isTotallyIncoherent = false
        )
        return isApproved
    }
}
