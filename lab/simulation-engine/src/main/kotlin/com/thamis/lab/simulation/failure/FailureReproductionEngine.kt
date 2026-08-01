package com.thamis.lab.simulation.failure

import com.thamis.lab.core.common.logging.LabLogger
import com.thamis.lab.simulation.scenario.Scenario
import com.thamis.lab.simulation.scenario.ScenarioBuilder

public data class ReproducibleFailureArtifact(
    public val failureId: String,
    public val originalScenarioId: String,
    public val logTrace: String,
    public val reproducibleScenario: Scenario
)

/**
 * Failure Reproduction Engine capturing raw failure traces, environment states, and constructing deterministic replay scenarios.
 */
public class FailureReproductionEngine {
    private val TAG = "FailureReproductionEngine"

    public fun captureAndBuildReproducibleScenario(failureId: String, originalScenarioId: String, logTrace: String): ReproducibleFailureArtifact {
        LabLogger.info(TAG, "Constructing reproducible replay scenario for failure '$failureId' (from $originalScenarioId)...")

        val replayScenario = ScenarioBuilder("replay-$failureId", "Reproduce Failure $failureId")
            .description("Deterministic replay for failure $failureId: $logTrace")
            .build()

        return ReproducibleFailureArtifact(
            failureId = failureId,
            originalScenarioId = originalScenarioId,
            logTrace = logTrace,
            reproducibleScenario = replayScenario
        )
    }
}
