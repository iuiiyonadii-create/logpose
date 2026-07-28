package com.uriel.logpose.thamis.hardening.reliability

import com.uriel.logpose.thamis.hardening.model.FailureReport
import com.uriel.logpose.thamis.hardening.model.FailureSeverity

/**
 * Clasifica y analiza las causas raíz de los fallos del sistema.
 */
object FailureAnalyzer {

    fun analyze(module: String, error: String): FailureReport {
        val severity = when {
            error.contains("CRITICAL") || error.contains("FATAL") -> FailureSeverity.CRITICAL
            error.contains("TIMEOUT") || error.contains("DISCONNECTED") -> FailureSeverity.HIGH
            else -> FailureSeverity.MEDIUM
        }

        return FailureReport(
            severity = severity,
            module = module,
            cause = error,
            impact = "System degradation in $module",
            recommendation = "Check logs and verify $module provider status."
        )
    }
}
