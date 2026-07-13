package com.uriel.logpose.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import androidx.annotation.RequiresPermission
import com.uriel.logpose.model.DeviceType
import com.uriel.logpose.model.LogPoseDevice

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
                    type = detectDeviceType(device),
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

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun detectDeviceType(
        device: BluetoothDevice
    ): DeviceType {

        val name = device.name
            ?.lowercase()
            .orEmpty()

        return when {

            "cardo" in name ||
                    "sena" in name ||
                    "freedconn" in name ||
                    "lexin" in name ||
                    "fodsports" in name ||
                    "interphone" in name ->
                DeviceType.INTERCOM

            "buds" in name ||
                    "airpods" in name ||
                    "headset" in name ||
                    "earbuds" in name ->
                DeviceType.HEADPHONES

            "car" in name ||
                    "vw" in name ||
                    "ford" in name ||
                    "chevrolet" in name ||
                    "fiat" in name ||
                    "renault" in name ||
                    "peugeot" in name ||
                    "toyota" in name ->
                DeviceType.CAR

            "speaker" in name ||
                    "jbl" in name ||
                    "sony" in name ->
                DeviceType.SPEAKER

            else ->
                DeviceType.UNKNOWN

        }

    }

}