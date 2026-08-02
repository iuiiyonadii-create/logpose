package com.thamis.lab.simulation.validation

import com.thamis.lab.core.contracts.decision.CognitiveDecision
import com.thamis.lab.simulation.scenario.Scenario

public data class ScenarioExecutionResult(
    public val scenarioId: String,
    public val isPassed: Boolean,
    public val actualIntentMatched: String?,
    public val expectedIntent: String?,
    public val executionDurationMs: Long,
    public val failureReason: String? = null
)

/**
 * Validation Engine for evaluating scenario execution outputs against expected behavior.
 */
public class ValidationEngine {

    public fun validateScenarioResult(
        scenario: Scenario,
        decision: CognitiveDecision?,
        durationMs: Long
    ): ScenarioExecutionResult {
        val expected = scenario.expectedIntentName
        val actual = decision?.intentName

        val isPassed = if (expected != null) {
            expected == actual && (decision?.isExecutable == true)
        } else {
            decision != null
        }

        val failureReason = if (!isPassed) {
            "Expected intent '$expected' but got '$actual' (decision executable=${decision?.isExecutable})"
        } else null

        return ScenarioExecutionResult(
            scenarioId = scenario.scenarioId,
            isPassed = isPassed,
            actualIntentMatched = actual,
            expectedIntent = expected,
            executionDurationMs = durationMs,
            failureReason = failureReason
        )
    }
}
