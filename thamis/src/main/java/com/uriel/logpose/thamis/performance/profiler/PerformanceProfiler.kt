package com.uriel.logpose.thamis.performance.profiler

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.performance.model.PerformanceEvent
import com.uriel.logpose.thamis.performance.telemetry.TelemetryEngine
import java.util.concurrent.ConcurrentHashMap

/**
 * Responsable de medir el rendimiento general y coordinar el perfilado.
 */
object PerformanceProfiler {
    private val startTimes = ConcurrentHashMap<String, Long>()

    fun startOperation(module: String, operation: String) {
        val key = "$module:$operation"
        startTimes[key] = System.currentTimeMillis()
        TelemetryEngine.record(PerformanceEvent.ModuleStarted(module))
    }

    fun finishOperation(module: String, operation: String, result: String = "SUCCESS") {
        val key = "$module:$operation"
        val startTime = startTimes.remove(key) ?: return
        val duration = System.currentTimeMillis() - startTime
        
        TelemetryEngine.record(PerformanceEvent.ModuleFinished(module, duration))
        
        if (duration > 500) {
            TelemetryEngine.record(PerformanceEvent.LatencyDetected(module, duration))
        }
        
        LogPoseLogger.d("THAMIS_PROFILER: [$module] $operation finished in ${duration}ms. Result: $result")
    }

    fun recordError(module: String, error: String) {
        TelemetryEngine.record(PerformanceEvent.ModuleFailed(module, error))
    }
}
