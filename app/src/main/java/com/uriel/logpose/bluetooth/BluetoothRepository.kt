package com.uriel.logpose.bluetooth

import android.content.Context
import com.uriel.logpose.model.LogPoseDevice
import com.uriel.logpose.storage.DevicePreferences

class BluetoothRepository(
    context: Context
) {

    private val bluetoothManager = BluetoothManager()

    private val permissionManager =
        BluetoothPermissionManager(context)

    private val devicePreferences =
        DevicePreferences(context)

    fun isBluetoothEnabled(): Boolean {

        if (!permissionManager.hasBluetoothPermission()) {
            return false
        }

        return bluetoothManager.isBluetoothEnabled()

    }

    fun getPairedDevices(): List<LogPoseDevice> {

        if (!permissionManager.hasBluetoothPermission()) {
            return emptyList()
        }

        return bluetoothManager.getPairedDevices()

    }

    fun saveSelectedDevice(mac: String) {
        devicePreferences.saveSelectedDevice(mac)
    }

    fun getSelectedDeviceMac(): String? {
        return devicePreferences.getSelectedDevice()
    }

    fun getSelectedDevice(): LogPoseDevice? {

        val mac = getSelectedDeviceMac() ?: return null

        return getPairedDevices().find {
            it.mac == mac
        }

    }

}