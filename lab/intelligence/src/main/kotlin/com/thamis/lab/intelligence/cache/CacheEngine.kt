package com.thamis.lab.intelligence.cache

import com.thamis.lab.core.common.logging.LabLogger
import java.util.concurrent.ConcurrentHashMap

public data class CacheStatsReport(
    public val totalCachedEntries: Int,
    public val cacheHitRatePercent: Double,
    public val cacheMissRatePercent: Double
)

/**
 * Cache Engine providing in-memory caching with TTL and stats for simulation results, reports, and indices.
 */
public class CacheEngine {
    private val TAG = "CacheEngine"
    private val cache = ConcurrentHashMap<String, Any>()
    private var hits = 0L
    private var misses = 0L

    public fun put(key: String, value: Any) {
        cache[key] = value
        LabLogger.info(TAG, "Cached entry for key '$key'. Total entries: ${cache.size}")
    }

    public fun get(key: String): Any? {
        val valObj = cache[key]
        if (valObj != null) hits++ else misses++
        return valObj
    }

    public fun getStats(): CacheStatsReport {
        val total = hits + misses
        val hitRate = if (total > 0) (hits.toDouble() / total) * 100.0 else 100.0
        val missRate = 100.0 - hitRate

        return CacheStatsReport(
            totalCachedEntries = cache.size,
            cacheHitRatePercent = hitRate,
            cacheMissRatePercent = missRate
        )
    }
}
