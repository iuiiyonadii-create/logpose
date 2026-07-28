package com.uriel.logpose.thamis.desktop_lab.model

import com.uriel.logpose.thamis.lab.model.SimulationScenario
import java.util.*

/**
 * Estado general de la estación de control del laboratorio.
 */
data class DesktopLabState(
    val isRunning: Boolean = false,
    val connectedDevices: Int = 0,
    val activeScenarioId: String? = null,
    val totalTestsRun: Int = 0,
    val totalErrorsDetected: Int = 0,
    val systemUptimeMs: Long = 0
)

/**
 * Representa un dashboard con métricas en tiempo real para la interfaz de escritorio.
 */
data class LabDashboardMetrics(
    val cpuUsage: Float,
    val ramUsageMb: Long,
    val latencyMs: Long,
    val voiceAccuracy: Float,
    val batteryImpactMa: Float
)

/**
 * Configuración de exportación de reportes.
 */
enum class ExportFormat { JSON, CSV, MARKDOWN }

/**
 * Entrada de log para la consola del laboratorio desktop.
 */
data class LabConsoleEntry(
    val timestamp: Long = System.currentTimeMillis(),
    val tag: String, // THAMIS_LAB, THAMIS_VOICE, etc.
    val message: String,
    val level: String // INFO, WARN, ERROR
)
