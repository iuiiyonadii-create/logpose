package com.uriel.logpose.features.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.IntentFilter
import android.util.Log
import com.uriel.logpose.core.compat.PermissionManager
import com.uriel.logpose.data.preferences.DevicePreferences
import com.uriel.logpose.domain.models.LogPoseDevice

class BluetoothRepository(
    context: Context
) {

    private val bluetoothManager = BluetoothManager()

    private val devicePreferences =
        DevicePreferences(context)

    private val appContext =
        context.applicationContext

    private var receiver: BluetoothReceiver? = null

    fun isBluetoothEnabled(): Boolean {

        val permission =
            PermissionManager.hasBluetoothPermission(appContext)

        Log.d(
            "LOGPOSE_PERMISSION",
            "Bluetooth permission = $permission"
        )

        if (!permission) {
            return false
        }

        return bluetoothManager.isBluetoothEnabled()

    }

    fun getPairedDevices(): List<LogPoseDevice> {

        val permission =
            PermissionManager.hasBluetoothPermission(appContext)

        Log.d(
            "LOGPOSE_PERMISSION",
            "PairedDevices permission = $permission"
        )

        if (!permission) {
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

        if (!PermissionManager.hasBluetoothPermission(appContext)) {
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

        appContext.registerReceiver(
            receiver,
            filter
        )

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

        val mac =
            getSelectedDeviceMac()
                ?: return null

        return getPairedDevices().find {
            it.mac == mac
        }

    }

}