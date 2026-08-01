package com.thamis.lab.timemachine

import com.thamis.lab.core.contracts.event.LabEvent
import com.thamis.lab.core.contracts.snapshot.CognitiveSnapshot
import com.thamis.lab.headless.clock.VirtualClock
import com.thamis.lab.headless.runner.HeadlessRunner
import com.thamis.lab.timemachine.replay.ReplayEngine
import com.thamis.lab.timemachine.snapshot.SnapshotManager

/**
 * Deterministic Time Machine facade providing full control over simulation time,
 * step execution, rewind via snapshots, and full event replay.
 */
public class DeterministicTimeMachine(
    public val masterSeed: Long = 42L,
    public val clock: VirtualClock = VirtualClock()
) {
    public val runner: HeadlessRunner = HeadlessRunner(masterSeed, clock)
    public val snapshotManager: SnapshotManager = SnapshotManager()
    public val replayEngine: ReplayEngine = ReplayEngine(runner)

    public fun pause() {
        clock.pause()
    }

    public fun resume() {
        clock.resume()
    }

    public fun setSpeedMultiplier(multiplier: Double) {
        clock.setSpeedMultiplier(multiplier)
    }

    public fun step(): LabEvent? {
        return runner.step()
    }

    public fun runUntil(targetTimeMs: Long): Int {
        return runner.runUntil(targetTimeMs)
    }

    public fun recordSnapshot(snapshot: CognitiveSnapshot) {
        snapshotManager.captureSnapshot(snapshot)
    }

    public fun rewindTo(timestampMs: Long): CognitiveSnapshot? {
        runner.reset()
        val snapshot = snapshotManager.findNearestSnapshotBefore(timestampMs)
        val targetTime = snapshot?.timestampMs ?: timestampMs
        clock.setTime(targetTime)
        return snapshot
    }

    public fun replay(events: List<LabEvent>): Int {
        return replayEngine.replayEvents(events)
    }

    public fun reset() {
        runner.reset()
        snapshotManager.stateStore.clear()
    }
}
