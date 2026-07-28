package com.uriel.logpose.thamis.hardware.metrics

/**
 * KPIs de estabilidad y rendimiento del hardware físico.
 */
object HardwareMetrics {
    var totalConnections = 0
    var connectionDrops = 0
    var averageAudioLatencyMs = 0L
    var compatibilityIssuesCount = 0

    fun recordConnection(ms: Long) {
        totalConnections++
        // logic
    }

    fun recordDrop() {
        connectionDrops++
    }

    fun recordLatency(ms: Long) {
        averageAudioLatencyMs = (averageAudioLatencyMs + ms) / 2
    }
}
