package com.thamis.lab.headless.runner

import com.thamis.lab.core.contracts.event.LabEvent
import com.thamis.lab.headless.clock.VirtualClock
import com.thamis.lab.headless.dispatcher.DeterministicEventDispatcher
import com.thamis.lab.headless.queue.DeterministicEventQueue
import com.thamis.lab.headless.seed.SeedManager

/**
 * Headless Execution Engine for deterministic simulation loops.
 */
public class HeadlessRunner(
    public val masterSeed: Long = 42L,
    public val clock: VirtualClock = VirtualClock()
) {
    public val seedManager: SeedManager = SeedManager(masterSeed)
    public val eventQueue: DeterministicEventQueue = DeterministicEventQueue()
    public val eventDispatcher: DeterministicEventDispatcher = DeterministicEventDispatcher()

    private var processedEventsCount: Long = 0L

    public val totalProcessedEvents: Long get() = processedEventsCount

    public fun enqueueEvent(event: LabEvent) {
        eventQueue.enqueue(event)
    }

    public fun step(): LabEvent? {
        if (clock.isPaused) return null
        val nextEvent = eventQueue.pollNext() ?: return null

        if (nextEvent.timestampMs > clock.currentTimeMs) {
            clock.setTime(nextEvent.timestampMs)
        }

        eventDispatcher.dispatch(nextEvent)
        processedEventsCount++
        return nextEvent
    }

    public fun runUntil(targetTimeMs: Long): Int {
        var stepsCount = 0
        while (!clock.isPaused && eventQueue.size() > 0) {
            val nextTime = eventQueue.peekNext()?.timestampMs ?: break
            if (nextTime > targetTimeMs) break
            step()
            stepsCount++
        }
        if (!clock.isPaused && clock.currentTimeMs < targetTimeMs) {
            clock.setTime(targetTimeMs)
        }
        return stepsCount
    }

    public fun reset(newSeed: Long = masterSeed) {
        clock.reset()
        seedManager.reset()
        eventQueue.clear()
        processedEventsCount = 0L
    }
}
