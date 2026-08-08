package com.thamis.lab.intelligence.engineering

public data class QualityScore(
    public val overallScore: Double,
    public val stabilityScore: Double,
    public val reliabilityScore: Double,
    public val robustnessScore: Double,
    public val latencyImpactScore: Double = 0.0
)

/**
 * Quality Engine for calculating objective quality, stability, reliability, and robustness scores.
 */
public class QualityEngine {

    public fun calculateScores(
        passedScenarios: Int,
        totalScenarios: Int,
        faultScenariosPassed: Int,
        totalFaultScenarios: Int,
        avgLatencyMs: Long = 0L
    ): QualityScore {
        if (totalScenarios <= 0) return QualityScore(0.0, 0.0, 0.0, 0.0)

        val reliability = passedScenarios.toDouble() / totalScenarios
        val robustness = if (totalFaultScenarios > 0) faultScenariosPassed.toDouble() / totalFaultScenarios else 1.0
        
        // Latency Impact (Penalty for latency > 100ms in voice commands)
        val latencyScore = if (avgLatencyMs > 0) {
            val ratio = 100.0 / avgLatencyMs.coerceAtLeast(100L)
            ratio.coerceAtMost(1.0)
        } else 1.0

        val stability = (reliability * 0.5) + (robustness * 0.3) + (latencyScore * 0.2)
        val overall = (stability * 100.0)

        return QualityScore(
            overallScore = overall,
            stabilityScore = stability * 100.0,
            reliabilityScore = reliability * 100.0,
            robustnessScore = robustness * 100.0,
            latencyImpactScore = latencyScore * 100.0
        )
    }
}
