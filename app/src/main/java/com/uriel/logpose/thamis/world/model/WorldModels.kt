package com.uriel.logpose.thamis.world.model

import java.util.*

/**
 * WorldModel v1.1: El estado total e inmutable del universo LogPose.
 * Optimizado para persistencia (Concrete Classes).
 */
data class WorldSnapshot(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val user: UserState = UserState(),
    val vehicle: VehicleState = VehicleState(),
    val environment: EnvironmentState = EnvironmentState(),
    val cognitive: CognitiveState = CognitiveState(),
    val systems: SystemStates = SystemStates()
)

data class UserState(
    val id: String = "rider_01",
    val attentionLevel: Float = 1.0f,
    val isSpeaking: Boolean = false,
    val lastInteraction: Long = 0
)

data class VehicleState(
    val speedKmh: Float = 0f,
    val acceleration: Float = 0f,
    val moving: Boolean = false,
    val riskLevel: RiskLevel = RiskLevel.LOW,
    val fuelLevelPct: Int = 100,
    val engineTempCelsius: Int = 85,
    val tirePressureOk: Boolean = true,
    val batteryVoltage: Float = 13.8f
)

enum class RiskLevel { LOW, MEDIUM, HIGH, CRITICAL }

data class EnvironmentState(
    val timeOfDay: String = "DAY",
    val weather: String = "CLEAR",
    val luminosityLux: Float = 0f
)

data class CognitiveState(
    val conversationState: String = "IDLE",
    val activeIntent: String? = null,
    val lastDecisionId: String? = null
)

data class SystemStates(
    val audio: AudioStateV1 = AudioStateV1(),
    val navigation: NavigationStateV1 = NavigationStateV1(),
    val communication: CommunicationStateV1 = CommunicationStateV1(),
    val device: DeviceStateV1 = DeviceStateV1()
)

data class AudioStateV1(
    val isPlaying: Boolean = false,
    val provider: String? = null,
    val volume: Int = 0,
    val isScoOpen: Boolean = false,
    val noiseLevel: Float = 0.0f // Misión v4.0: Staff Ear support
)

enum class GpsStatus { UNAVAILABLE, SEARCHING, READY }

data class NavigationStateV1(
    val isNavigating: Boolean = false,
    val destination: String? = null,
    val etaMs: Long = 0,
    val gpsAvailable: Boolean = false,
    val accuracyMeters: Float = 0f,
    val gpsStatus: GpsStatus = GpsStatus.UNAVAILABLE
)

data class CommunicationStateV1(
    val isCallActive: Boolean = false,
    val pendingMessages: Int = 0,
    val lastNotificationApp: String? = null
)

data class DeviceStateV1(
    val batteryPct: Int = 0,
    val isCharging: Boolean = false,
    val bluetoothConnected: Boolean = false,
    val internetAvailable: Boolean = false
)
