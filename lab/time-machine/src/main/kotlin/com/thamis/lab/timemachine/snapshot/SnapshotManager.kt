package com.thamis.lab.timemachine.snapshot

import com.thamis.lab.core.contracts.snapshot.CognitiveSnapshot
import com.thamis.lab.timemachine.store.DeterministicStateStore

/**
 * Manages simulation snapshots for rewind and fast restoration.
 */
public class SnapshotManager(public val stateStore: DeterministicStateStore = DeterministicStateStore()) {

    public fun captureSnapshot(snapshot: CognitiveSnapshot) {
        stateStore.saveSnapshot(snapshot)
    }

    public fun restoreSnapshot(timestampMs: Long): CognitiveSnapshot? {
        return stateStore.getSnapshot(timestampMs)
    }

    public fun findNearestSnapshotBefore(timestampMs: Long): CognitiveSnapshot? {
        return stateStore.getAllSnapshots()
            .filter { it.timestampMs <= timestampMs }
            .maxByOrNull { it.timestampMs }
    }
}
