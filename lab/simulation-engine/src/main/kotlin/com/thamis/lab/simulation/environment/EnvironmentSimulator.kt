package com.thamis.lab.simulation.environment

import com.thamis.lab.core.contracts.snapshot.AudioState
import com.thamis.lab.core.contracts.snapshot.BatteryState
import com.thamis.lab.core.contracts.snapshot.BluetoothState
import com.thamis.lab.core.contracts.snapshot.CognitiveSnapshot
import com.thamis.lab.core.contracts.snapshot.LocationState

/**
 * Environment Simulator Facade for simulating physical and system context state changes.
 */
public class EnvironmentSimulator(initialSnapshot: CognitiveSnapshot = CognitiveSnapshot(timestampMs = 0L)) {
    private var currentSnapshotState = initialSnapshot

    public val currentSnapshot: CognitiveSnapshot get() = currentSnapshotState

    public fun simulateBluetoothConnection(deviceName: String, isIntercom: Boolean = true): CognitiveSnapshot {
        val updatedBt = BluetoothState(isConnected = true, deviceName = deviceName, isIntercom = isIntercom)
        currentSnapshotState = currentSnapshotState.copy(bluetoothState = updatedBt)
        return currentSnapshotState
    }

    public fun simulateBluetoothDisconnect(): CognitiveSnapshot {
        val updatedBt = BluetoothState(isConnected = false, deviceName = null, isIntercom = false)
        currentSnapshotState = currentSnapshotState.copy(bluetoothState = updatedBt)
        return currentSnapshotState
    }

    public fun simulateGpsSpeed(speedKmH: Double, lat: Double = 0.0, lon: Double = 0.0): CognitiveSnapshot {
        val updatedLoc = LocationState(speedKmH = speedKmH, latitude = lat, longitude = lon)
        currentSnapshotState = currentSnapshotState.copy(locationState = updatedLoc)
        return currentSnapshotState
    }

    public fun simulateAudioPlayback(isPlaying: Boolean, appName: String? = null): CognitiveSnapshot {
        val updatedAudio = AudioState(isPlaying = isPlaying, currentMediaApp = appName, volumePercent = 80)
        currentSnapshotState = currentSnapshotState.copy(audioState = updatedAudio)
        return currentSnapshotState
    }

    public fun simulateBatteryDrop(levelPercent: Int): CognitiveSnapshot {
        val updatedBatt = BatteryState(levelPercent = levelPercent, isCharging = false)
        currentSnapshotState = currentSnapshotState.copy(batteryState = updatedBatt)
        return currentSnapshotState
    }

    public fun simulateActiveApp(appName: String): CognitiveSnapshot {
        currentSnapshotState = currentSnapshotState.copy(activeApp = appName)
        return currentSnapshotState
    }

    public fun reset(snapshot: CognitiveSnapshot = CognitiveSnapshot(timestampMs = 0L)) {
        currentSnapshotState = snapshot
    }
}
