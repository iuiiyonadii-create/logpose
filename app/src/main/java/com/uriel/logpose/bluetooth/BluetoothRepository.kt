package com.uriel.logpose.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
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
        onFinished: () -> Unit,
        onDeviceConnected: ((LogPoseDevice) -> Unit)? = null,
        onDeviceDisconnected: ((LogPoseDevice) -> Unit)? = null
    ) {

        if (!permissionManager.hasBluetoothPermission()) {
            onFinished()
            return
        }

        stopDiscovery()

        receiver = BluetoothReceiver(
            onDeviceFound = onDeviceFound,
            onDiscoveryFinished = onFinished,
            onDeviceConnected = onDeviceConnected,
            onDeviceDisconnected = onDeviceDisconnected
        )

        val filter = IntentFilter().apply {

            addAction(BluetoothDevice.ACTION_FOUND)

            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)

            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)

            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)

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