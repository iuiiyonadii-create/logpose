package com.thamis.lab.intelligence.quality

public data class QualityScore(
    public val overallScore: Double,
    public val stabilityScore: Double,
    public val reliabilityScore: Double,
    public val robustnessScore: Double
)

/**
 * Quality Engine for calculating objective quality, stability, reliability, and robustness scores.
 */
public class QualityEngine {

    public fun calculateScores(
        passedScenarios: Int,
        totalScenarios: Int,
        faultScenariosPassed: Int,
        totalFaultScenarios: Int
    ): QualityScore {
        if (totalScenarios <= 0) return QualityScore(0.0, 0.0, 0.0, 0.0)

        val reliability = passedScenarios.toDouble() / totalScenarios
        val robustness = if (totalFaultScenarios > 0) faultScenariosPassed.toDouble() / totalFaultScenarios else 1.0
        val stability = (reliability * 0.6) + (robustness * 0.4)
        val overall = (stability * 100.0)

        return QualityScore(
            overallScore = overall,
            stabilityScore = stability * 100.0,
            reliabilityScore = reliability * 100.0,
            robustnessScore = robustness * 100.0
        )
    }
}
