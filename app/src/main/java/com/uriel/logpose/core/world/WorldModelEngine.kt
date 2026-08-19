package com.uriel.logpose.core.world

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class WorldSnapshot(
    val id: String = java.util.UUID.randomUUID().toString(),
    val timestampMs: Long = System.currentTimeMillis(),
    val speedKmh: Double = 0.0,
    val tripActive: Boolean = false,
    val weatherCondition: String = "Normal",
    val rainRisk: Boolean = false,
    val attributes: Map<String, String> = emptyMap()
)

object WorldModelEngine {

    private val mutex = Mutex()
    private var currentSnapshot: WorldSnapshot = WorldSnapshot()

    suspend fun getSnapshot(): WorldSnapshot {
        return mutex.withLock { currentSnapshot }
    }

    /**
     * Actualiza el modelo de mundo aplicando un reducer de forma segura
     * y atómica serializada a través de un Mutex.
     */
    suspend fun update(reducer: (WorldSnapshot) -> WorldSnapshot): WorldSnapshot {
        return mutex.withLock {
            val updated = reducer(currentSnapshot)
            currentSnapshot = updated
            updated
        }
    }

    suspend fun reset() {
        mutex.withLock {
            currentSnapshot = WorldSnapshot()
        }
    }
}
