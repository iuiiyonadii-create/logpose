package com.uriel.logpose.thamis.multiagent

/**
 * Representa una propuesta de mejora de código generada por THAMIS LAB.
 */
data class CodePatch(
    val id: String,
    val targetFile: String,
    val description: String,
    val suggestedChange: String,
    val riskLevel: RiskLevel = RiskLevel.LOW
) {
    enum class RiskLevel { LOW, MEDIUM, HIGH, CRITICAL }
}
