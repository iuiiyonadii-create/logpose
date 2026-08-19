package com.uriel.logpose.thamis.performance.metrics

/**
 * Métricas globales de performance.
 */
object PerformanceMetrics {
    var averageGlobalLatencyMs = 0.0
    var maxLatencyMs = 0L
    var totalEvents = 0
    var totalErrors = 0
    var stabilityIndex = 1.0f

    fun update(latency: Long, isError: Boolean) {
        totalEvents++
        if (isError) totalErrors++
        
        averageGlobalLatencyMs = (averageGlobalLatencyMs * (totalEvents - 1) + latency) / totalEvents
        if (latency > maxLatencyMs) maxLatencyMs = latency
        
        stabilityIndex = if (totalEvents > 0) 1.0f - (totalErrors.toFloat() / totalEvents) else 1.0f
    }
}
