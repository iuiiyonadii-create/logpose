package com.uriel.logpose.features.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
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

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun getPairedDevices(): List<LogPoseDevice> {

        val bondedDevices = bluetoothAdapter
            ?.bondedDevices
            .orEmpty()

        val connectedMac = bondedDevices
            .firstOrNull {
                it.bondState == BluetoothDevice.BOND_BONDED
            }
            ?.address

        return bondedDevices
            .map { device ->

                LogPoseDevice(
                    mac = device.address,
                    name = device.name ?: "Desconocido",
                    type = DeviceClassifier.detect(
                        device.name.orEmpty()
                    ),
                    connected = device.address == connectedMac
                )

            }
            .sortedBy { it.name }

    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun startDiscovery(): Boolean {

        val adapter = bluetoothAdapter ?: return false

        if (adapter.isDiscovering) {
            adapter.cancelDiscovery()
        }

        return adapter.startDiscovery()

    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun cancelDiscovery() {

        bluetoothAdapter?.let { adapter ->

            if (adapter.isDiscovering) {
                adapter.cancelDiscovery()
            }

        }

    }

}