package com.thamis.lab.simulation.fault

import com.thamis.lab.core.contracts.event.LabEvent
import com.thamis.lab.headless.seed.SeedManager

public enum class FaultType {
    BLUETOOTH_DROP,
    GPS_SIGNAL_LOSS,
    NETWORK_DISCONNECT,
    LOW_BATTERY,
    STT_NOISE_CORRUPTION
}

/**
 * Fault Injection Engine for injecting deterministic or random faults into test streams.
 */
public class FaultInjectionEngine(public val seedManager: SeedManager = SeedManager(42L)) {
    private val injectedFaults = mutableListOf<LabEvent.FaultInjectedEvent>()

    public val activeFaults: List<LabEvent.FaultInjectedEvent> get() = injectedFaults.toList()

    public fun injectDeterministicFault(eventId: String, timestampMs: Long, faultType: FaultType, targetComponent: String): LabEvent.FaultInjectedEvent {
        val event = LabEvent.FaultInjectedEvent(
            eventId = eventId,
            timestampMs = timestampMs,
            faultType = faultType.name,
            targetComponent = targetComponent
        )
        injectedFaults.add(event)
        return event
    }

    public fun injectRandomFault(timestampMs: Long, targetComponent: String): LabEvent.FaultInjectedEvent {
        val types = FaultType.values()
        val index = seedManager.nextInt(types.size)
        val selectedFault = types[index]
        return injectDeterministicFault("fault-${seedManager.nextLong()}", timestampMs, selectedFault, targetComponent)
    }

    public fun clearFaults() {
        injectedFaults.clear()
    }
}
