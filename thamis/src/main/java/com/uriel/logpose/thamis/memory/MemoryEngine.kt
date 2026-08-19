package com.uriel.logpose.thamis.memory

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * FASE 26.10 — LOGPOSE THAMIS MEMORY SYSTEM
 * FASE 1: MEMORY ENGINE CORE
 */
object MemoryEngine {

    private val volatileMemory = mutableMapOf<String, Any>()

    fun remember(key: String, value: Any) {
        volatileMemory[key] = value
        LogPoseLogger.d("MemoryEngine: Guardando '$key' en memoria temporal.")
    }

    fun retrieve(key: String): Any? {
        return volatileMemory[key]
    }

    fun forget(key: String) {
        volatileMemory.remove(key)
    }

    fun clearAll() {
        volatileMemory.clear()
        LogPoseLogger.i("MemoryEngine: Memoria temporal limpiada.")
    }
}
