package com.uriel.logpose.thamis.lab.model

import java.util.*

/**
 * Representa un escenario de simulación en el laboratorio.
 */
interface LabScenario {
    val id: String
    val name: String
    val description: String
}

data class NetworkScenario(
    override val id: String = UUID.randomUUID().toString(),
    override val name: String = "Network Scenario",
    override val description: String,
    val latencyMs: Long,
    val packetLossRate: Float, // 0.0 to 1.0
    val isConnected: Boolean
) : LabScenario

data class BluetoothScenario(
    override val id: String = UUID.randomUUID().toString(),
    override val name: String = "Bluetooth Scenario",
    override val description: String,
    val state: String, // CONNECTED, DISCONNECTED, RECONNECTING
    val deviceCount: Int
) : LabScenario

data class VoiceScenario(
    override val id: String = UUID.randomUUID().toString(),
    override val name: String = "Voice Scenario",
    override val description: String,
    val noiseType: String, // WIND, MOTOR, STREET, CLEAN
    val noiseLevel: Float // 0.0 to 1.0
) : LabScenario

data class BatteryScenario(
    override val id: String = UUID.randomUUID().toString(),
    override val name: String = "Battery Scenario",
    override val description: String,
    val batteryPct: Int,
    val isCharging: Boolean,
    val isPowerSaveMode: Boolean
) : LabScenario

data class MemoryScenario(
    override val id: String = UUID.randomUUID().toString(),
    override val name: String = "Memory Scenario",
    override val description: String,
    val availableRamMb: Long,
    val isLowMemory: Boolean
) : LabScenario

data class GPSScenario(
    override val id: String = UUID.randomUUID().toString(),
    override val name: String = "GPS Scenario",
    override val description: String,
    val signalStrength: Float, // 0.0 to 1.0
    val satellitesCount: Int,
    val isLocked: Boolean
) : LabScenario

/**
 * Reporte de fallo detectado durante la simulación.
 */
data class LabFailureReport(
    val scenarioId: String,
    val scenarioName: String,
    val failedModule: String,
    val recoveryTimeMs: Long,
    val impact: String, // LOW, MEDIUM, HIGH, CRITICAL
    val timestamp: Long = System.currentTimeMillis()
)
