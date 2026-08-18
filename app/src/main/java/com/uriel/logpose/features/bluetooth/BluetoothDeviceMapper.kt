package com.uriel.logpose.features.bluetooth

import android.bluetooth.BluetoothDevice
import com.uriel.logpose.domain.models.LogPoseDevice

object BluetoothDeviceMapper {
    @Suppress("MissingPermission")
    fun mapToDomain(device: BluetoothDevice): LogPoseDevice {
        return LogPoseDevice(
            name = device.name ?: "Unknown Device",
            mac = device.address,
            connected = false // Connection state is handled elsewhere
        )
    }
}
