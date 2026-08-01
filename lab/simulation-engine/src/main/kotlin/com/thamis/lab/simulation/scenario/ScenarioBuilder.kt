package com.thamis.lab.simulation.scenario

import com.thamis.lab.core.contracts.event.LabEvent
import com.thamis.lab.core.contracts.snapshot.CognitiveSnapshot

/**
 * Fluent DSL Builder for creating reproducible Scenario instances.
 */
public class ScenarioBuilder(
    private val scenarioId: String,
    private val name: String
) {
    private var description: String = ""
    private var initialSnapshot: CognitiveSnapshot = CognitiveSnapshot(timestampMs = 0L)
    private val events = mutableListOf<LabEvent>()
    private var expectedIntentName: String? = null
    private var maxDurationMs: Long = 10000L

    public fun description(desc: String): ScenarioBuilder = apply { this.description = desc }
    public fun initialSnapshot(snapshot: CognitiveSnapshot): ScenarioBuilder = apply { this.initialSnapshot = snapshot }
    public fun addEvent(event: LabEvent): ScenarioBuilder = apply { this.events.add(event) }
    public fun expectedIntent(intent: String): ScenarioBuilder = apply { this.expectedIntentName = intent }
    public fun maxDuration(durationMs: Long): ScenarioBuilder = apply { this.maxDurationMs = durationMs }

    public fun build(): Scenario {
        return Scenario(
            scenarioId = scenarioId,
            name = name,
            description = description,
            initialSnapshot = initialSnapshot,
            events = events.sortedBy { it.timestampMs },
            expectedIntentName = expectedIntentName,
            maxDurationMs = maxDurationMs
        )
    }
}
