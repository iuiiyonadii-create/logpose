package com.uriel.logpose.thamis.world.model

import java.util.*

/**
 * WorldModel v1.0: El estado total e inmutable del universo LogPose.
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
    val riskLevel: RiskLevel = RiskLevel.LOW
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
    val audio: AudioSystemState = AudioStateV1(),
    val navigation: NavigationSystemState = NavigationStateV1(),
    val communication: CommunicationSystemState = CommunicationStateV1(),
    val device: DeviceSystemState = DeviceStateV1()
)

interface AudioSystemState {
    val isPlaying: Boolean
    val provider: String?
    val volume: Int
    val isScoOpen: Boolean
}

data class AudioStateV1(
    override val isPlaying: Boolean = false,
    override val provider: String? = null,
    override val volume: Int = 0,
    override val isScoOpen: Boolean = false
) : AudioSystemState

enum class GpsStatus { UNAVAILABLE, SEARCHING, READY }

interface NavigationSystemState {
    val isNavigating: Boolean
    val destination: String?
    val etaMs: Long
    val gpsAvailable: Boolean
    val accuracyMeters: Float
    val gpsStatus: GpsStatus
}

data class NavigationStateV1(
    override val isNavigating: Boolean = false,
    override val destination: String? = null,
    override val etaMs: Long = 0,
    override val gpsAvailable: Boolean = false,
    override val accuracyMeters: Float = 0f,
    override val gpsStatus: GpsStatus = GpsStatus.UNAVAILABLE
) : NavigationSystemState

interface CommunicationSystemState {
    val isCallActive: Boolean
    val pendingMessages: Int
    val lastNotificationApp: String?
}

data class CommunicationStateV1(
    override val isCallActive: Boolean = false,
    override val pendingMessages: Int = 0,
    override val lastNotificationApp: String? = null
) : CommunicationSystemState

interface DeviceSystemState {
    val batteryPct: Int
    val isCharging: Boolean
    val bluetoothConnected: Boolean
    val internetAvailable: Boolean
}

data class DeviceStateV1(
    override val batteryPct: Int = 0,
    override val isCharging: Boolean = false,
    override val bluetoothConnected: Boolean = false,
    override val internetAvailable: Boolean = false
) : DeviceSystemState
