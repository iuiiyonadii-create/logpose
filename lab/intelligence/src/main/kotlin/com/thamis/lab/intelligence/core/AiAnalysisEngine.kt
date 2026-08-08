package com.thamis.lab.intelligence.core

import com.thamis.lab.core.contracts.decision.CognitiveDecision
import com.thamis.lab.simulation.validation.ScenarioExecutionResult

public data class AnalysisReport(
    public val scenarioId: String,
    public val isStable: Boolean,
    public val performanceScore: Double,
    public val behaviorSummary: String
)

/**
 * AI Analysis Engine for logs, errors, scenario execution, performance, and stability.
 */
public class AiAnalysisEngine {

    public fun analyzeExecution(
        result: ScenarioExecutionResult,
        decisions: List<CognitiveDecision>
    ): AnalysisReport {
        val isStable = result.isPassed
        val avgConfidence = if (decisions.isNotEmpty()) decisions.map { it.confidenceScore }.average() else 1.0
        val perfScore = if (result.executionDurationMs < 50) 1.0 else 0.8

        val summary = "Scenario ${result.scenarioId} executed in ${result.executionDurationMs}ms with avg confidence $avgConfidence"

        return AnalysisReport(
            scenarioId = result.scenarioId,
            isStable = isStable,
            performanceScore = perfScore * avgConfidence,
            behaviorSummary = summary
        )
    }
}
