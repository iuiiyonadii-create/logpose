package com.uriel.logpose.thamis.monitoring.telemetry

import com.uriel.logpose.thamis.monitoring.model.PerformanceSnapshot
import java.util.Collections
import javax.inject.Inject
import javax.inject.Singleton

/**
 * TelemetryCollector v2.0: Arquitectura DI (Misión #115) + Límite de 50 claves en mapas de logs.
 */
@Singleton
class TelemetryCollector @Inject constructor() {
    
    companion object {
        private const val MAX_ENTRIES = 50
        private var instance: TelemetryCollector? = null

        fun get(): TelemetryCollector {
            return instance ?: synchronized(this) {
                instance ?: TelemetryCollector().also { instance = it }
            }
        }

        fun recordPerformance(module: String, timeMs: Long) = get().recordPerformance(module, timeMs)
        fun recordError(module: String) = get().recordError(module)
        fun getPerformanceHistory(module: String): List<PerformanceSnapshot> = get().getPerformanceHistory(module)
        fun getErrorCount(module: String): Int = get().getErrorCount(module)
    }

    init {
        instance = this
    }

    // LinkedHashMap con orden de acceso para limitar a 50 claves máximas
    private val performanceLogs = Collections.synchronizedMap(
        object : LinkedHashMap<String, MutableList<PerformanceSnapshot>>(MAX_ENTRIES, 0.75f, true) {
            override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, MutableList<PerformanceSnapshot>>?): Boolean {
                return size > MAX_ENTRIES
            }
        }
    )

    private val errorLogs = Collections.synchronizedMap(
        object : LinkedHashMap<String, Int>(MAX_ENTRIES, 0.75f, true) {
            override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Int>?): Boolean {
                return size > MAX_ENTRIES
            }
        }
    )

    fun recordPerformance(module: String, timeMs: Long) {
        val snapshot = PerformanceSnapshot(module, timeMs)
        synchronized(performanceLogs) {
            val list = performanceLogs.getOrPut(module) { mutableListOf() }
            list.add(snapshot)
            if (list.size > 100) {
                list.removeAt(0)
            }
        }
    }

    fun recordError(module: String) {
        synchronized(errorLogs) {
            errorLogs[module] = (errorLogs[module] ?: 0) + 1
        }
    }

    fun getPerformanceHistory(module: String): List<PerformanceSnapshot> = synchronized(performanceLogs) {
        performanceLogs[module]?.toList() ?: emptyList()
    }

    fun getErrorCount(module: String): Int = synchronized(errorLogs) {
        errorLogs[module] ?: 0
    }

    fun clear() {
        performanceLogs.clear()
        errorLogs.clear()
    }
}
