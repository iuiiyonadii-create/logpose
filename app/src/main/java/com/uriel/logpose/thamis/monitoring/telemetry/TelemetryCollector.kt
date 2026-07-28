package com.uriel.logpose.thamis.monitoring.telemetry

import com.uriel.logpose.thamis.monitoring.model.PerformanceSnapshot
import java.util.concurrent.ConcurrentHashMap

/**
 * Colector de datos brutos de ejecución de todos los módulos.
 */
object TelemetryCollector {
    private val performanceLogs = ConcurrentHashMap<String, MutableList<PerformanceSnapshot>>()
    private val errorLogs = ConcurrentHashMap<String, Int>()

    fun recordPerformance(module: String, timeMs: Long) {
        val snapshot = PerformanceSnapshot(module, timeMs)
        performanceLogs.getOrPut(module) { mutableListOf() }.add(snapshot)
        
        // Limitar historial por módulo
        if (performanceLogs[module]!!.size > 100) {
            performanceLogs[module]!!.removeAt(0)
        }
    }

    fun recordError(module: String) {
        errorLogs[module] = (errorLogs[module] ?: 0) + 1
    }

    fun getPerformanceHistory(module: String): List<PerformanceSnapshot> = 
        performanceLogs[module]?.toList() ?: emptyList()

    fun getErrorCount(module: String): Int = errorLogs[module] ?: 0

    fun clear() {
        performanceLogs.clear()
        errorLogs.clear()
    }
}
