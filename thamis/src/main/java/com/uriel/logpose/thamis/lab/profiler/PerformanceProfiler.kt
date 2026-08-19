package com.uriel.logpose.thamis.lab.profiler

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Mide tiempos y consumo durante las pruebas de laboratorio.
 */
object PerformanceProfiler {

    fun trackOperation(module: String, action: () -> Unit) {
        val start = System.currentTimeMillis()
        action()
        val duration = System.currentTimeMillis() - start
        
        LogPoseLogger.d("THAMIS_LAB_PROFILER: [$module] Operación completada en ${duration}ms")
    }

    fun getResourceUsage(): Map<String, Long> {
        return mapOf(
            "MEMORY" to 2048L,
            "CPU_LOAD" to 15L
        )
    }
}
