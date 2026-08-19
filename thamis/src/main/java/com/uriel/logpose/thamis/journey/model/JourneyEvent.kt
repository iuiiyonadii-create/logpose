package com.uriel.logpose.thamis.journey.model

/**
 * Eventos que disparan transiciones en el Journey Engine.
 */
sealed class JourneyEvent {
    object BluetoothConnected : JourneyEvent()
    object BluetoothDisconnected : JourneyEvent()
    object HelmetConnected : JourneyEvent()
    object HelmetDisconnected : JourneyEvent()
    data class SpeedChanged(val speed: Float) : JourneyEvent()
    data class MovementDetected(val isMoving: Boolean) : JourneyEvent()
    object GPSLost : JourneyEvent()
    object GPSRestored : JourneyEvent()
    object ManualStart : JourneyEvent()
    object ManualStop : JourneyEvent()
    object IdleTimeout : JourneyEvent()
}
