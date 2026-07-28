package com.uriel.logpose.thamis.lab.monitor

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.lab.model.*
import com.uriel.logpose.thamis.lab.simulation.SimulationEngine

/**
 * Motor principal del laboratorio de simulación THAMIS v1.0.
 */
object ThamisLabEngine {

    fun runQuickChaosTest() {
        LogPoseLogger.i("THAMIS_LAB: Iniciando prueba de caos rápida.")
        
        val scenarios = listOf(
            NetworkScenario(description = "Latencia alta", latencyMs = 2000L, packetLossRate = 0.3f, isConnected = true),
            BluetoothScenario(description = "Desconexión repentina", state = "DISCONNECTED", deviceCount = 1),
            GPSScenario(description = "Señal débil", signalStrength = 0.2f, satellitesCount = 2, isLocked = false)
        )

        scenarios.forEach { scenario ->
            SimulationEngine.executeScenario(scenario)
        }
    }
}
