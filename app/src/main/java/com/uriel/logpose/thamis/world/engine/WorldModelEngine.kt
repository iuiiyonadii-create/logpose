package com.uriel.logpose.thamis.world.engine

import com.uriel.logpose.thamis.world.model.WorldSnapshot
import com.uriel.logpose.thamis.world.history.WorldHistory
import com.uriel.logpose.thamis.world.audit.WorldAudit
import com.uriel.logpose.thamis.world.audit.WorldTrace
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * WorldModelEngine v1.0: Única fuente de verdad inmutable de THAMIS.
 */
object WorldModelEngine {

    private var currentSnapshot = WorldSnapshot()

    /**
     * Actualiza el modelo del mundo. Solo este método puede mutar el estado.
     */
    fun update(domain: String, reducer: (WorldSnapshot) -> WorldSnapshot) {
        val startTime = System.currentTimeMillis()
        val oldSnapshot = currentSnapshot
        val newSnapshot = reducer(currentSnapshot).copy(timestamp = startTime)
        
        currentSnapshot = newSnapshot
        WorldHistory.add(newSnapshot)
        
        // Auditoría
        WorldAudit.record(WorldTrace(
            snapshotId = newSnapshot.id,
            affectedDomain = domain,
            description = "Update from $domain",
            latencyMs = System.currentTimeMillis() - startTime
        ))

        LogPoseLogger.d("[THAMIS_WORLD] Model updated by $domain. Snapshot ID: ${newSnapshot.id}")
    }

    /**
     * Devuelve el snapshot más reciente. Inmutable.
     */
    fun getCurrentSnapshot(): WorldSnapshot = currentSnapshot

    fun getHistory(): List<WorldSnapshot> = WorldHistory.getFullHistory()
}
