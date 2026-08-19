package com.uriel.logpose.thamis.planning.metrics

/**
 * KPIs de rendimiento del motor de planificación.
 */
object PlanningMetrics {
    var totalPlans = 0
    var averagePlanningMs = 0L
    var totalCancellations = 0
    var totalRetries = 0

    fun recordPlanning(latency: Long) {
        totalPlans++
        averagePlanningMs = (averagePlanningMs * (totalPlans - 1) + latency) / totalPlans
    }

    fun recordCancellation() { totalCancellations++ }
    fun recordRetry() { totalRetries++ }
}
