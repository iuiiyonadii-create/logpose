package com.uriel.logpose.thamis.hardening.reliability

import com.uriel.logpose.thamis.hardening.model.ReliabilityStats

/**
 * Motor encargado de medir y predecir la fiabilidad del sistema.
 */
object ReliabilityEngine {
    private var startTime = System.currentTimeMillis()
    private var totalFailures = 0
    private var totalRecoveryTime = 0L

    fun recordFailure(recoveryTimeMs: Long) {
        totalFailures++
        totalRecoveryTime += recoveryTimeMs
    }

    fun calculateStats(): ReliabilityStats {
        val uptime = System.currentTimeMillis() - startTime
        val availability = if (uptime > 0) (uptime.toFloat() - totalRecoveryTime) / uptime else 1.0f
        
        return ReliabilityStats(
            uptimeMs = uptime,
            availabilityPct = availability * 100,
            meanTimeToFailureMs = if (totalFailures > 0) uptime / totalFailures else uptime,
            meanTimeToRecoveryMs = if (totalFailures > 0) totalRecoveryTime / totalFailures else 0
        )
    }
}
