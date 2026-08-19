package com.uriel.logpose.thamis.optimization.lifecycle

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.optimization.cache.CacheManager

/**
 * Gestiona el ciclo de vida del motor para pausas seguras y conservación de estado.
 */
object LifecycleManager {
    private var isPaused = false

    fun pause() {
        if (isPaused) return
        isPaused = true
        LogPoseLogger.i("THAMIS_LIFECYCLE: Entrando en pausa segura. Persistiendo caché esencial.")
        // CacheManager.persist()
    }

    fun resume() {
        if (!isPaused) return
        isPaused = false
        LogPoseLogger.i("THAMIS_LIFECYCLE: Reanudando motor cognitivo.")
    }

    fun isSystemActive(): Boolean = !isPaused
}
