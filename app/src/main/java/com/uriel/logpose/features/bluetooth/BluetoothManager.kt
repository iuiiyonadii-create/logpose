package com.uriel.logpose.features.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.annotation.RequiresPermission
import com.uriel.logpose.core.compat.core.DeviceClassifier
import com.uriel.logpose.domain.models.LogPoseDevice

class BluetoothManager {

    @Suppress("DEPRECATION")
    private val bluetoothAdapter: BluetoothAdapter? =
        BluetoothAdapter.getDefaultAdapter()

    fun isBluetoothAvailable(): Boolean {
        return bluetoothAdapter != null
    }

    @Suppress("MissingPermission", "DEPRECATION")
    @RequiresPermission(anyOf = [
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH
    ])
    fun isBluetoothEnabled(): Boolean {

        val adapter = bluetoothAdapter

        Log.d(
            "LOGPOSE_BT",
            "Adapter = $adapter"
        )

        Log.d(
            "LOGPOSE_BT",
            "Enabled = ${adapter?.isEnabled}"
        )

        Log.d(
            "LOGPOSE_BT",
            "State = ${adapter?.state}"
        )

        return adapter?.isEnabled == true

    }

    @Suppress("MissingPermission", "DEPRECATION")
    @RequiresPermission(anyOf = [
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH
    ])
    fun getPairedDevices(): List<LogPoseDevice> {

        val bondedDevices =
            bluetoothAdapter?.bondedDevices.orEmpty()

        val connectedMac =
            bondedDevices.firstOrNull()?.address

        Log.d(
            "LOGPOSE_BT",
            "Bonded devices = ${bondedDevices.size}"
        )

        return bondedDevices.map { device ->

            Log.d(
                "LOGPOSE_BT",
                "Device: ${device.name} (${device.address})"
            )

            LogPoseDevice(
                mac = device.address,
                name = device.name ?: "Desconocido",
                type = DeviceClassifier.detect(
                    device.name.orEmpty()
                ),
                connected = device.address == connectedMac
            )

        }.sortedBy { it.name }

    }

    @Suppress("MissingPermission", "DEPRECATION")
    @RequiresPermission(anyOf = [
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH
    ])
    fun startDiscovery(): Boolean {

        val adapter = bluetoothAdapter ?: return false

        if (adapter.isDiscovering) {
            adapter.cancelDiscovery()
        }

        Log.d(
            "LOGPOSE_BT",
            "Starting discovery..."
        )

        return adapter.startDiscovery()

    }

    @Suppress("MissingPermission", "DEPRECATION")
    @RequiresPermission(anyOf = [
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH
    ])
    fun cancelDiscovery() {

        bluetoothAdapter?.let { adapter ->

            if (adapter.isDiscovering) {

                Log.d(
                    "LOGPOSE_BT",
                    "Cancel discovery"
                )

                adapter.cancelDiscovery()

            }

        }

    }

}