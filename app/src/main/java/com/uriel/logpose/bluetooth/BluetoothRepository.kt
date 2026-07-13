package com.uriel.logpose.bluetooth

import android.content.Context
import android.content.IntentFilter
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

    private val appContext = context.applicationContext

    private var receiver: BluetoothReceiver? = null

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

    fun startDiscovery(
        onDeviceFound: (LogPoseDevice) -> Unit,
        onFinished: () -> Unit
    ) {

        if (!permissionManager.hasBluetoothPermission()) {
            onFinished()
            return
        }

        stopDiscovery()

        receiver = BluetoothReceiver(
            onDeviceFound = onDeviceFound,
            onDiscoveryFinished = onFinished
        )

        val filter = IntentFilter().apply {
            addAction(android.bluetooth.BluetoothDevice.ACTION_FOUND)
            addAction(android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }

        appContext.registerReceiver(receiver, filter)

        bluetoothManager.startDiscovery()
    }

    fun stopDiscovery() {

        bluetoothManager.cancelDiscovery()

        receiver?.let {
            runCatching {
                appContext.unregisterReceiver(it)
            }
        }

        receiver = null
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