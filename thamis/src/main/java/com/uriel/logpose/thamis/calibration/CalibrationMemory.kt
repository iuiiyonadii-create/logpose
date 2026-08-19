package com.uriel.logpose.thamis.calibration

import java.util.concurrent.CopyOnWriteArrayList

/**
 * Persiste los reportes de calibración para medir la evolución de THAMIS.
 */
object CalibrationMemory {

    private val reportHistory = CopyOnWriteArrayList<CalibrationReport>()

    fun store(report: CalibrationReport) {
        reportHistory.add(report)
    }

    fun getHistory(): List<CalibrationReport> = reportHistory.toList()

    fun getEvolutionSummary(): String {
        if (reportHistory.isEmpty()) return "Sin historial de calibración."
        
        return reportHistory.joinToString("\n") { 
            "v${it.engineVersion}: ${it.accuracyPercentage}% Accuracy (${it.totalSamples} muestras)"
        }
    }
}
