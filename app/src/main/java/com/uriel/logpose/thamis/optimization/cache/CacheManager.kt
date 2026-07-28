package com.uriel.logpose.thamis.optimization.cache

import java.util.concurrent.ConcurrentHashMap

/**
 * Gestión de datos temporales y expiración segura.
 */
object CacheManager {
    private val cache = ConcurrentHashMap<String, CacheEntry>()

    data class CacheEntry(val data: Any, val timestamp: Long, val ttlMs: Long)

    fun put(key: String, data: Any, ttlMs: Long = 60000L) {
        cache[key] = CacheEntry(data, System.currentTimeMillis(), ttlMs)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String): T? {
        val entry = cache[key] ?: return null
        if (System.currentTimeMillis() - entry.timestamp > entry.ttlMs) {
            cache.remove(key)
            return null
        }
        return entry.data as? T
    }

    fun cleanup() {
        val now = System.currentTimeMillis()
        cache.entries.removeIf { now - it.value.timestamp > it.value.ttlMs }
    }

    fun clearAll() = cache.clear()
}
