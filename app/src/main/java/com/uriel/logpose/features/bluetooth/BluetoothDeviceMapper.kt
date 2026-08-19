package com.uriel.logpose.features.bluetooth

import android.bluetooth.BluetoothDevice
import com.uriel.logpose.domain.models.LogPoseDevice

object BluetoothDeviceMapper {
    fun mapToDomain(device: BluetoothDevice): LogPoseDevice {
        val deviceName = try {
            device.name ?: "Unknown Device"
        } catch (_: SecurityException) {
            "Unknown Device"
        }
        return LogPoseDevice(
            name = deviceName,
            mac = device.address,
            connected = false // Connection state is handled elsewhere
        )
    }
}
