package com.uriel.logpose.thamis.world.engine

import com.uriel.logpose.thamis.world.model.WorldSnapshot
import com.uriel.logpose.thamis.world.history.WorldHistory
import com.uriel.logpose.thamis.world.audit.WorldAudit
import com.uriel.logpose.thamis.world.audit.WorldTrace
import com.uriel.logpose.core.compat.core.LogPoseLogger
import android.content.Context
import com.google.gson.Gson
import com.uriel.logpose.features.diagnostics.ProactiveDiagnosticsEngine

/**
 * WorldModelEngine v1.2: Hardened Persistence (Misión #010).
 * Asegura la supervivencia del estado ante cierres forzados del SO.
 */
object WorldModelEngine {

    private var currentSnapshot = WorldSnapshot()
    private val gson = Gson()

    fun update(domain: String, reducer: (WorldSnapshot) -> WorldSnapshot) {
        val startTime = System.currentTimeMillis()
        val newSnapshot = reducer(currentSnapshot).copy(timestamp = startTime)
        
        currentSnapshot = newSnapshot
        
        // --- PROACTIVE ENGINE v4.0 ---
        ProactiveDiagnosticsEngine.check(newSnapshot)
        
        WorldHistory.add(newSnapshot)
        
        WorldAudit.record(WorldTrace(
            snapshotId = newSnapshot.id,
            affectedDomain = domain,
            description = "Update from $domain",
            latencyMs = System.currentTimeMillis() - startTime
        ))

        LogPoseLogger.d("[THAMIS_WORLD] Model updated by $domain. Snapshot ID: ${newSnapshot.id}")
        
        saveCheckpoint(newSnapshot)
    }

    fun getCurrentSnapshot(): WorldSnapshot = currentSnapshot

    private fun saveCheckpoint(snapshot: WorldSnapshot) {
        try {
            val app = com.uriel.logpose.core.app.LogPoseApplication.instance
            val prefs = app.getSharedPreferences("thamis_world_checkpoint", Context.MODE_PRIVATE)
            
            // Misión #010: Usamos commit() para garantizar escritura física antes de un posible crash
            val json = gson.toJson(snapshot)
            prefs.edit().putString("last_snapshot_json", json).commit()
            LogPoseLogger.d("WorldModel: Checkpoint guardado físicamente (${json.length} bytes)")
        } catch (e: Exception) {
            LogPoseLogger.e("WorldModel: Error al guardar checkpoint: ${e.message}")
        }
    }

    fun restoreFromCheckpoint(): Boolean {
        try {
            val app = com.uriel.logpose.core.app.LogPoseApplication.instance
            val prefs = app.getSharedPreferences("thamis_world_checkpoint", Context.MODE_PRIVATE)
            val json = prefs.getString("last_snapshot_json", null)
            
            if (json == null) {
                LogPoseLogger.d("WorldModel: No se encontró checkpoint previo.")
                return false
            }
            
            val restored = gson.fromJson(json, WorldSnapshot::class.java)
            currentSnapshot = restored
            LogPoseLogger.i("WorldModel: Estado restaurado exitosamente (ID: ${restored.id})")
            return true
        } catch (e: Exception) {
            LogPoseLogger.e("WorldModel: Fallo crítico al restaurar checkpoint: ${e.message}")
            return false
        }
    }

    fun getHistory(): List<WorldSnapshot> = WorldHistory.getFullHistory()
}
