package com.uriel.logpose.thamis.intelligence.validation

/**
 * FASE 27.0 — THAMIS AUTONOMOUS ENGINEERING CORE
 * FASE 14: RISK ANALYZER
 */
object RiskAnalyzer {

    enum class RiskLevel { LOW, MEDIUM, HIGH }

    fun evaluateRisk(proposal: String): RiskLevel {
        return if (proposal.contains("Security")) RiskLevel.LOW else RiskLevel.MEDIUM
    }
}
