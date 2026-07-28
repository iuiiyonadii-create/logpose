package com.uriel.logpose.thamis.lab.report

import com.uriel.logpose.thamis.lab.model.LabFailureReport

/**
 * Genera reportes técnicos detallados tras detectar fallos en simulación.
 */
object FailureReporter {

    fun generateReport(report: LabFailureReport): String {
        return """
            THAMIS LAB FAILURE REPORT
            --------------------------
            Escenario: ${report.scenarioName}
            Módulo Fallido: ${report.failedModule}
            Tiempo de Recuperación: ${report.recoveryTimeMs}ms
            Impacto: ${report.impact}
        """.trimIndent()
    }
}
