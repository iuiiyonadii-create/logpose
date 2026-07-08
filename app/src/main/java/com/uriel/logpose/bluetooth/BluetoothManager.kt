package com.uriel.logpose.bluetooth

import android.bluetooth.BluetoothAdapter
import androidx.annotation.RequiresPermission
import com.uriel.logpose.model.DeviceType
import com.uriel.logpose.model.LogPoseDevice

class BluetoothManager {

    private val bluetoothAdapter: BluetoothAdapter? =
        BluetoothAdapter.getDefaultAdapter()

    fun isBluetoothAvailable(): Boolean {
        return bluetoothAdapter != null
    }

    @RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    @RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
    fun getPairedDevices(): List<LogPoseDevice> {

        val devices = mutableListOf<LogPoseDevice>()

        bluetoothAdapter?.bondedDevices?.forEach { device ->

            devices.add(
                LogPoseDevice(
                    mac = device.address,
                    name = device.name ?: "Desconocido",
                    type = DeviceType.UNKNOWN
                )
            )

        }

        return devices
    }
}