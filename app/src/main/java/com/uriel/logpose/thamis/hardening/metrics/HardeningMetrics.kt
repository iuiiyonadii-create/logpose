package com.uriel.logpose.thamis.hardening.metrics

/**
 * KPIs de endurecimiento y consolidación.
 */
object HardeningMetrics {
    var crashRate = 0.0f
    var averageLatencyMs = 0L
    var memoryBaselineKb = 0L
    var batteryBaselineMa = 0f
    var totalErrors = 0
    var totalRecoveries = 0

    fun recordError() { totalErrors++ }
    fun recordRecovery() { totalRecoveries++ }
}
