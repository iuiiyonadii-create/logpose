package com.uriel.logpose.thamis.world.engine

import com.uriel.logpose.thamis.world.model.WorldSnapshot
import com.uriel.logpose.thamis.world.history.WorldHistory
import com.uriel.logpose.thamis.world.audit.WorldAudit
import com.uriel.logpose.thamis.world.audit.WorldTrace
import com.uriel.logpose.core.compat.core.LogPoseLogger
import android.content.Context
import com.google.gson.Gson
import com.uriel.logpose.features.diagnostics.ProactiveDiagnosticsEngine
import com.uriel.logpose.data.local.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * WorldModelEngine v2.0: Arquitectura Room + DI (Misión #115) + Mutex de Concurrencia.
 */
@Singleton
class WorldModelEngine @Inject constructor(
    private val logPoseDao: LogPoseDao
) {

    companion object {
        private var instance: WorldModelEngine? = null

        fun getCurrentSnapshot(): WorldSnapshot {
            return instance?.getCurrentSnapshot() ?: WorldSnapshot()
        }

        fun update(domain: String, reducer: (WorldSnapshot) -> WorldSnapshot) {
            instance?.update(domain, reducer)
        }
    }

    init {
        instance = this
    }

    private val mutex = Mutex()
    private var currentSnapshot = WorldSnapshot()
    private val gson = Gson()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun update(domain: String, reducer: (WorldSnapshot) -> WorldSnapshot) {
        scope.launch {
            val (newSnapshot, startTime) = mutex.withLock {
                val start = System.currentTimeMillis()
                val snapshot = reducer(currentSnapshot).copy(timestamp = start)
                currentSnapshot = snapshot
                Pair(snapshot, start)
            }

            ProactiveDiagnosticsEngine.check(newSnapshot)
            WorldHistory.add(newSnapshot)

            WorldAudit.record(WorldTrace(
                snapshotId = newSnapshot.id,
                affectedDomain = domain,
                description = "Update from $domain",
                latencyMs = System.currentTimeMillis() - startTime
            ))

            com.uriel.logpose.core.services.LogPoseHudService.updateWorld(newSnapshot)
            saveCheckpoint(newSnapshot)
        }
    }

    suspend fun updateAsync(domain: String, reducer: (WorldSnapshot) -> WorldSnapshot): WorldSnapshot {
        val (newSnapshot, startTime) = mutex.withLock {
            val start = System.currentTimeMillis()
            val snapshot = reducer(currentSnapshot).copy(timestamp = start)
            currentSnapshot = snapshot
            Pair(snapshot, start)
        }

        ProactiveDiagnosticsEngine.check(newSnapshot)
        WorldHistory.add(newSnapshot)

        WorldAudit.record(WorldTrace(
            snapshotId = newSnapshot.id,
            affectedDomain = domain,
            description = "Update from $domain",
            latencyMs = System.currentTimeMillis() - startTime
        ))

        com.uriel.logpose.core.services.LogPoseHudService.updateWorld(newSnapshot)
        saveCheckpoint(newSnapshot)
        return newSnapshot
    }

    fun getCurrentSnapshot(): WorldSnapshot = currentSnapshot

    private fun saveCheckpoint(snapshot: WorldSnapshot) {
        scope.launch {
            try {
                val json = gson.toJson(snapshot)
                logPoseDao.saveCheckpoint(WorldSnapshotCheckpointEntity("last_snapshot", json))
            } catch (e: Exception) {
                LogPoseLogger.e("WorldModel", "Error al guardar checkpoint en Room: ${e.message}")
            }
        }
    }

    fun restoreFromCheckpoint(context: Context): Boolean {
        // Migración desde SharedPreferences si existe
        val prefs = context.getSharedPreferences("thamis_world_checkpoint", Context.MODE_PRIVATE)
        val legacyJson = prefs.getString("last_snapshot_json", null)
        
        if (legacyJson != null) {
            LogPoseLogger.w("WorldModel", "Migrando checkpoint de SharedPreferences a Room...")
            currentSnapshot = gson.fromJson(legacyJson, WorldSnapshot::class.java)
            saveCheckpoint(currentSnapshot)
            prefs.edit().clear().apply()
            return true
        }

        // Carga normal desde Room (bloqueante para restauración inicial)
        return runBlocking {
            try {
                val json = logPoseDao.getCheckpoint("last_snapshot")
                if (json != null) {
                    currentSnapshot = gson.fromJson(json, WorldSnapshot::class.java)
                    LogPoseLogger.i("WorldModel", "Estado restaurado desde Room.")
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                false
            }
        }
    }

    fun getHistory(): List<WorldSnapshot> = WorldHistory.getFullHistory()
}
