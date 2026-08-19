package com.uriel.logpose.core.telemetry

import java.util.Collections

object TelemetryCollector {

    private const val MAX_ENTRIES = 50

    // LinkedHashMap con orden de acceso y límite FIFO de 50 entradas
    private val _performanceLogs = Collections.synchronizedMap(
        object : LinkedHashMap<String, String>(MAX_ENTRIES, 0.75f, true) {
            override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, String>?): Boolean {
                return size > MAX_ENTRIES
            }
        }
    )

    private val _errorLogs = Collections.synchronizedMap(
        object : LinkedHashMap<String, String>(MAX_ENTRIES, 0.75f, true) {
            override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, String>?): Boolean {
                return size > MAX_ENTRIES
            }
        }
    )

    val performanceLogs: Map<String, String>
        get() = synchronized(_performanceLogs) { _performanceLogs.toMap() }

    val errorLogs: Map<String, String>
        get() = synchronized(_errorLogs) { _errorLogs.toMap() }

    fun logPerformance(key: String, value: String) {
        _performanceLogs[key] = value
    }

    fun logError(key: String, value: String) {
        _errorLogs[key] = value
    }

    fun clear() {
        _performanceLogs.clear()
        _errorLogs.clear()
    }
}
