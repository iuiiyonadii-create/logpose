package com.uriel.logpose.thamis.desktop_lab.dashboard

import com.uriel.logpose.thamis.desktop_lab.app.DesktopLabEngine
import com.uriel.logpose.thamis.desktop_lab.model.LabDashboardMetrics

/**
 * Lógica del Dashboard principal para la estación de escritorio.
 */
object ThamisLabDashboard {

    fun refreshMetrics(): LabDashboardMetrics {
        val state = DesktopLabEngine.getStatus()
        
        // Simulación de lectura de telemetría para el UI
        return LabDashboardMetrics(
            cpuUsage = 15.5f,
            ramUsageMb = 240,
            latencyMs = 120,
            voiceAccuracy = 0.92f,
            batteryImpactMa = 110f
        )
    }
}
