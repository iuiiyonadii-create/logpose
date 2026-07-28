package com.uriel.logpose.thamis.desktop_lab.export

import com.uriel.logpose.thamis.desktop_lab.model.ExportFormat
import com.uriel.logpose.thamis.lab.model.SimulationReport

/**
 * Encargado de exportar los resultados de las pruebas a archivos de escritorio.
 */
object ReportExporter {

    fun export(report: SimulationReport, format: ExportFormat): String {
        return when (format) {
            ExportFormat.JSON -> "{ \"scenario\": \"${report.scenarioName}\", \"status\": \"SUCCESS\" }"
            ExportFormat.CSV -> "Scenario,TotalEvents,RecoveryTime\n${report.scenarioName},${report.totalEvents},${report.recoveryTimeMs}"
            ExportFormat.MARKDOWN -> "# Report: ${report.scenarioName}\n- Events: ${report.totalEvents}"
        }
    }
}
