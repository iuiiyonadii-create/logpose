package com.thamis.lab.simulation.scenario.generator

import com.thamis.lab.core.contracts.event.LabEvent
import com.thamis.lab.simulation.scenario.Scenario
import com.thamis.lab.simulation.scenario.ScenarioBuilder

/**
 * Scenario Mutator applying stochastic edge-case parameter mutations (injecting fault events, noise surges, speed spikes).
 */
public class ScenarioMutator {

    public fun mutateScenario(original: Scenario, mutationId: String): Scenario {
        val mutatedEvents = original.events.toMutableList()

        // Inject sudden Bluetooth Drop or Noise corruption event
        mutatedEvents.add(
            LabEvent.FaultInjectedEvent(
                eventId = "fault-$mutationId",
                timestampMs = original.initialSnapshot.timestampMs + 50L,
                faultType = "BLUETOOTH_DROP",
                targetComponent = "BluetoothManager"
            )
        )

        val builder = ScenarioBuilder("${original.scenarioId}-mutated-$mutationId", "${original.name} (Mutated)")
            .description("${original.description} [MUTATED with BLUETOOTH_DROP]")
            .initialSnapshot(original.initialSnapshot)

        for (evt in mutatedEvents) {
            builder.addEvent(evt)
        }

        if (original.expectedIntentName != null) {
            builder.expectedIntent(original.expectedIntentName!!)
        }

        return builder.build()
    }
}
