package com.uriel.logpose.features.diagnostics

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.features.voice.FeedbackManager

/**
 * VehicleDiagnosticsManager: Motor de diagnóstico de la moto (Misión #019).
 * Simula la telemetría de hardware para dar reportes de estado.
 */
object VehicleDiagnosticsManager {

    fun getFullStatus() {
        LogPoseLogger.i("Diagnostics: Ejecutando chequeo general...")
        val report = "Moto en estado óptimo. Presión de cubiertas nominal. Batería al cien por ciento."
        FeedbackManager.speak(report)
    }

    fun getFuelLevel() {
        LogPoseLogger.i("Diagnostics: Consultando nivel de nafta...")
        val autonomy = 240 // km
        val report = "Tenés medio tanque de nafta. Autonomía aproximada de $autonomy kilómetros."
        FeedbackManager.speak(report)
    }

    fun getMaintenanceInfo() {
        LogPoseLogger.i("Diagnostics: Consultando mantenimiento...")
        val nextService = 1500 // km
        val report = "El próximo service es en $nextService kilómetros. Las pastillas de freno están a media vida."
        FeedbackManager.speak(report)
    }

    fun getEngineTemperature() {
        LogPoseLogger.i("Diagnostics: Consultando temperatura...")
        val temp = 85 // Celsius
        val report = "La temperatura del motor es de $temp grados. Rango de operación normal."
        FeedbackManager.speak(report)
    }
}
