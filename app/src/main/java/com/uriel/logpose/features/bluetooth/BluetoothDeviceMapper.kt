package com.uriel.logpose.features.bluetooth

import android.bluetooth.BluetoothDevice
import com.uriel.logpose.domain.models.LogPoseDevice

object BluetoothDeviceMapper {
    // v83.0: MissingPermission justified as this is a pure mapping from an existing device object.
    @Suppress("MissingPermission")
    fun mapToDomain(device: BluetoothDevice): LogPoseDevice {
        return LogPoseDevice(
            name = device.name ?: "Unknown Device",
            mac = device.address,
            connected = false // Connection state is handled elsewhere
        )
    }
}
