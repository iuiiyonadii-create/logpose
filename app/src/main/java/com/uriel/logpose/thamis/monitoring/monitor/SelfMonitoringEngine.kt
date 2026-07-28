package com.uriel.logpose.thamis.monitoring.monitor

import com.uriel.logpose.thamis.monitoring.model.DiagnosticReport
import com.uriel.logpose.thamis.monitoring.diagnostic.DiagnosticEngine
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Engine central de auto-monitoreo de THAMIS.
 */
object SelfMonitoringEngine {

    fun runDiagnostic(): DiagnosticReport {
        LogPoseLogger.d("THAMIS_MONITOR: Iniciando ciclo de autoevaluación...")
        val report = DiagnosticEngine.generateReport()
        
        LogPoseLogger.i("THAMIS_HEALTH: ${report.summary}")
        if (report.issuesFound.isNotEmpty()) {
            LogPoseLogger.w("THAMIS_DIAGNOSTIC: Se encontraron ${report.issuesFound.size} problemas.")
        }
        
        return report
    }
}
