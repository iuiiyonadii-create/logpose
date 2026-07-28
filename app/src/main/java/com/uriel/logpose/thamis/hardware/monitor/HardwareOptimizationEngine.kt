package com.uriel.logpose.thamis.hardware.monitor

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.hardware.bluetooth.BluetoothAnalyzer
import com.uriel.logpose.thamis.hardware.audio.AudioLatencyAnalyzer
import com.uriel.logpose.thamis.hardware.intercom.IntercomProfileManager

/**
 * Motor central de optimización específica para el hardware del motociclista.
 */
object HardwareOptimizationEngine {

    fun runHardwareAudit(deviceName: String) {
        LogPoseLogger.i("THAMIS_HARDWARE: Iniciando auditoría física para $deviceName")
        
        val stability = BluetoothAnalyzer.getStabilityScore()
        val audioReport = AudioLatencyAnalyzer.generateReport()
        val profile = IntercomProfileManager.getProfileForDevice(deviceName)

        if (audioReport.latencyMs > profile.averageLatencyMs + 100) {
            LogPoseLogger.w("THAMIS_HARDWARE: Latencia de audio por encima del perfil esperado para $deviceName")
        }

        LogPoseLogger.d("THAMIS_HARDWARE: Auditoría completada. Estabilidad BT: ${stability * 100}%")
    }
}
