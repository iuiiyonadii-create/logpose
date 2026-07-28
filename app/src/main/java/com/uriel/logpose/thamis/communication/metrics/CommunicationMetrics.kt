package com.uriel.logpose.thamis.communication.metrics

/**
 * Métricas de performance y precisión del dominio de comunicación.
 */
object CommunicationMetrics {
    var totalRequests = 0
    var executions = 0
    var confirmed = 0
    var denied = 0
    var totalLatencyMs = 0L

    fun recordRequest(action: String, latency: Long) {
        totalRequests++
        totalLatencyMs += latency
        // logic for action mapping
    }

    fun getAverageLatency(): Long {
        return if (totalRequests > 0) totalLatencyMs / totalRequests else 0
    }
}
