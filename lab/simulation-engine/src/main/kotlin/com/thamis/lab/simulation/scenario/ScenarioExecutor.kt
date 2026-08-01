package com.thamis.lab.simulation.scenario

import com.thamis.lab.core.contracts.decision.CognitiveDecision
import com.thamis.lab.core.contracts.explanation.CognitiveExplanation
import com.thamis.lab.headless.runner.HeadlessRunner
import com.thamis.lab.simulation.environment.EnvironmentSimulator
import com.thamis.lab.simulation.validation.ScenarioExecutionResult
import com.thamis.lab.simulation.validation.ValidationEngine

/**
 * Scenario Executor for executing complete simulation scenarios through HeadlessRunner.
 */
public class ScenarioExecutor(
    public val runner: HeadlessRunner = HeadlessRunner(),
    public val validationEngine: ValidationEngine = ValidationEngine()
) {
    public val environmentSimulator: EnvironmentSimulator = EnvironmentSimulator()

    public fun executeScenario(scenario: Scenario): ScenarioExecutionResult {
        runner.reset()
        environmentSimulator.reset(scenario.initialSnapshot)

        for (event in scenario.events) {
            runner.enqueueEvent(event)
        }

        val startTime = System.currentTimeMillis()
        runner.runUntil(scenario.maxDurationMs)
        val duration = System.currentTimeMillis() - startTime

        // Simulated decision for scenario testing (In real integration, this calls THAMIS engine)
        val lastEventText = scenario.events
            .filterIsInstance<com.thamis.lab.core.contracts.event.LabEvent.TextCommandEvent>()
            .lastOrNull()?.userText

        val mockDecision = if (lastEventText != null) {
            val intent = if (lastEventText.contains("música")) "PLAY_MUSIC" else "UNKNOWN"
            CognitiveDecision(
                intentName = intent,
                confidenceScore = 0.95,
                actionCommand = "MEDIA_PLAY",
                isExecutable = true,
                explanation = CognitiveExplanation("Matched text '$lastEventText'", listOf("TEXT_EVENT"), listOf("SAFETY_CHECK"), 0.95)
            )
        } else null

        return validationEngine.validateScenarioResult(scenario, mockDecision, duration)
    }
}
