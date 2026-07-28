package com.uriel.logpose.thamis.lab.model

import java.util.*

/**
 * Representa el estado simulado de un dispositivo físico.
 */
data class VirtualDeviceState(
    val batteryPct: Int = 100,
    val temperature: Float = 35.0f,
    val availableMemoryMb: Long = 4096,
    val cpuLoad: Float = 0.1f,
    val isCharging: Boolean = false,
    val networkState: NetworkQuality = NetworkQuality.STABLE
)

enum class NetworkQuality { STABLE, SLOW, DISCONNECTED, RECOVERING }

/**
 * Tipos de eventos del mundo real simulables.
 */
sealed class RealWorldEvent {
    data class IncomingCall(val callerName: String, val phoneNumber: String) : RealWorldEvent()
    data class NewMessage(val sender: String, val content: String, val isPriority: Boolean) : RealWorldEvent()
    data class WeatherChange(val condition: String, val temp: Float) : RealWorldEvent()
    data class NavigationUpdate(val nextInstruction: String, val distanceMeters: Int) : RealWorldEvent()
    data class MultimediaEvent(val action: String, val trackName: String?) : RealWorldEvent()
    data class BluetoothEvent(val state: String, val deviceName: String) : RealWorldEvent()
}

/**
 * Escenario de simulación completo.
 */
data class SimulationScenario(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val eventSequence: List<RealWorldEvent>,
    val environmentalConditions: Map<String, Any> = emptyMap()
)

/**
 * Registro de una prueba de voz comparativa.
 */
data class VoiceTestEntry(
    val originalPhrase: String,
    val recognizedPhrase: String,
    val correction: String?,
    val context: String,
    val conditions: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Reporte consolidado de una sesión de simulación.
 */
data class SimulationReport(
    val scenarioName: String,
    val totalEvents: Int,
    val thamisResponses: List<String>,
    val errorsDetected: List<String>,
    val recoveryTimeMs: Long,
    val performanceSummary: String
)
