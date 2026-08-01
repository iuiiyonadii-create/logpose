package com.thamis.lab.timemachine.store

import com.thamis.lab.core.contracts.snapshot.CognitiveSnapshot
import java.util.concurrent.ConcurrentHashMap

/**
 * Thread-safe State Store for saving and querying cognitive snapshots by timestamp.
 */
public class DeterministicStateStore {
    private val snapshots = ConcurrentHashMap<Long, CognitiveSnapshot>()

    public fun saveSnapshot(snapshot: CognitiveSnapshot) {
        snapshots[snapshot.timestampMs] = snapshot
    }

    public fun getSnapshot(timestampMs: Long): CognitiveSnapshot? {
        return snapshots[timestampMs]
    }

    public fun getAllSnapshots(): List<CognitiveSnapshot> {
        return snapshots.values.sortedBy { it.timestampMs }
    }

    public fun clear() {
        snapshots.clear()
    }
}
