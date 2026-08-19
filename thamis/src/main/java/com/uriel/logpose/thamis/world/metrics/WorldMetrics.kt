package com.uriel.logpose.thamis.world.metrics

/**
 * KPIs de rendimiento del modelo del mundo.
 */
object WorldMetrics {
    var totalSnapshots = 0
    var averageUpdateLatencyMs = 0L
    var invalidStatesCount = 0

    fun recordUpdate(latency: Long) {
        totalSnapshots++
        averageUpdateLatencyMs = (averageUpdateLatencyMs * (totalSnapshots - 1) + latency) / totalSnapshots
    }

    fun recordInvalidState() {
        invalidStatesCount++
    }
}
