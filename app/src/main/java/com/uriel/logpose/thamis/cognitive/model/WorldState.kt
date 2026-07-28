package com.uriel.logpose.thamis.cognitive.model

/**
 * La realidad externa percibida por THAMIS.
 * Este objeto es puramente descriptivo.
 */
data class WorldState(
    val driving: DrivingState,
    val system: SystemState,
    val external: ExternalState
) {
    data class DrivingState(
        val isMoving: Boolean = false,
        val speedKmh: Int = 0,
        val hasActiveNavigation: Boolean = false,
        val gpsAvailable: Boolean = true
    )

    data class SystemState(
        val isMusicPlaying: Boolean = false,
        val isHeadsetConnected: Boolean = false,
        val activeCall: Boolean = false,
        val hasCriticalAlert: Boolean = false,
        val batteryPct: Int = 100
    )

    data class ExternalState(
        val weatherCondition: String? = null,
        val temperature: Float? = null
    )
}
