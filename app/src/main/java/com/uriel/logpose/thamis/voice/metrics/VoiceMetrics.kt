package com.uriel.logpose.thamis.voice.metrics

/**
 * KPIs de inteligencia vocal.
 */
object VoiceMetrics {
    var totalRequests = 0
    var averageConfidence = 0f
    var totalErrors = 0
    var averageLatencyMs = 0L
    private var confidenceSum = 0f

    fun recordInteraction(confidence: Float, latency: Long, isError: Boolean) {
        totalRequests++
        confidenceSum += confidence
        averageConfidence = confidenceSum / totalRequests
        averageLatencyMs = (averageLatencyMs * (totalRequests - 1) + latency) / totalRequests
        if (isError) totalErrors++
    }
}
