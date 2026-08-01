package com.thamis.lab.simulation.scenario

import com.thamis.lab.core.contracts.event.LabEvent
import com.thamis.lab.core.contracts.snapshot.CognitiveSnapshot

/**
 * Immutable Scenario definition for deterministic simulation execution.
 */
public data class Scenario(
    public val scenarioId: String,
    public val name: String,
    public val description: String = "",
    public val initialSnapshot: CognitiveSnapshot,
    public val events: List<LabEvent> = emptyList(),
    public val expectedIntentName: String? = null,
    public val maxDurationMs: Long = 10000L
)
