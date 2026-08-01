package com.thamis.lab.timemachine.replay

import com.thamis.lab.core.contracts.event.LabEvent
import com.thamis.lab.headless.runner.HeadlessRunner

/**
 * Deterministically replays recorded event streams through HeadlessRunner.
 */
public class ReplayEngine(public val runner: HeadlessRunner) {

    public fun replayEvents(events: List<LabEvent>): Int {
        runner.reset()
        for (event in events) {
            runner.enqueueEvent(event)
        }
        val maxTime = events.maxOfOrNull { it.timestampMs } ?: 0L
        return runner.runUntil(maxTime)
    }
}
