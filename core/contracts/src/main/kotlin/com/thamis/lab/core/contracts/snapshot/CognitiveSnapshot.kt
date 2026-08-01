package com.thamis.lab.core.contracts.snapshot

/**
 * Immutable snapshot of system, user, and environmental state.
 */
public data class CognitiveSnapshot(
    public val timestampMs: Long,
    public val audioState: AudioState = AudioState(),
    public val bluetoothState: BluetoothState = BluetoothState(),
    public val locationState: LocationState = LocationState(),
    public val batteryState: BatteryState = BatteryState(),
    public val activeApp: String? = null
)

public data class AudioState(
    public val isPlaying: Boolean = false,
    public val currentMediaApp: String? = null,
    public val volumePercent: Int = 50
)

public data class BluetoothState(
    public val isConnected: Boolean = false,
    public val deviceName: String? = null,
    public val isIntercom: Boolean = false
)

public data class LocationState(
    public val speedKmH: Double = 0.0,
    public val latitude: Double = 0.0,
    public val longitude: Double = 0.0
)

public data class BatteryState(
    public val levelPercent: Int = 100,
    public val isCharging: Boolean = false
)
