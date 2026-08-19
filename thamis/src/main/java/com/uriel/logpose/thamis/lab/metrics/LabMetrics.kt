package com.uriel.logpose.thamis.lab.metrics

/**
 * KPIs y recolector de datos del laboratorio.
 */
object LabMetrics {
    var totalSimulations = 0
    var failureDetectedCount = 0
    var averageRecoveryTimeMs = 0L

    fun recordSimulation(recoveryTime: Long, failed: Boolean) {
        totalSimulations++
        if (failed) failureDetectedCount++
        averageRecoveryTimeMs = (averageRecoveryTimeMs + recoveryTime) / 2
    }
}
