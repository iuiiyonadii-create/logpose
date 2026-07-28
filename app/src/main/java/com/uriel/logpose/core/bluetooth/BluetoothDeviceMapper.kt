package com.uriel.logpose.core.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.uriel.logpose.domain.models.LogPoseDevice
import com.uriel.logpose.domain.models.DeviceType

/**
 * Maps Android BluetoothDevice to domain LogPoseDevice.
 */
object BluetoothDeviceMapper {

    @SuppressLint("MissingPermission")
    fun mapToDomain(device: BluetoothDevice): LogPoseDevice {
        return LogPoseDevice(
            name = device.name ?: "Unknown Device",
            mac = device.address,
            type = DeviceType.HEADPHONES // Default to HEADPHONES for domain mapping
        )
    }
}
