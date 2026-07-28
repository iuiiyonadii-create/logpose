package com.uriel.logpose.thamis.integration.validation

/**
 * KPIs del sistema nervioso central de THAMIS.
 */
object IntegrationMetrics {
    var totalRequests = 0
    var successfulPipelines = 0
    var averageLatencyMs = 0L
    var conflictsResolved = 0

    fun record(latency: Long, success: Boolean) {
        totalRequests++
        if (success) successfulPipelines++
        averageLatencyMs = (averageLatencyMs * (totalRequests - 1) + latency) / totalRequests
    }

    fun recordConflict() {
        conflictsResolved++
    }
}
