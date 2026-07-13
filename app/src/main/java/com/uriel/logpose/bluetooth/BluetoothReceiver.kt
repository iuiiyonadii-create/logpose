package com.uriel.logpose.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import com.uriel.logpose.model.DeviceType
import com.uriel.logpose.model.LogPoseDevice

class BluetoothReceiver(
    private val onDeviceFound: (LogPoseDevice) -> Unit,
    private val onDiscoveryFinished: () -> Unit
) : BroadcastReceiver() {

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onReceive(
        context: Context?,
        intent: Intent?
    ) {

        when (intent?.action) {

            BluetoothDevice.ACTION_FOUND -> {

                val device = intent.getParcelableExtra<BluetoothDevice>(
                    BluetoothDevice.EXTRA_DEVICE
                ) ?: return

                onDeviceFound(
                    LogPoseDevice(
                        mac = device.address,
                        name = device.name ?: "Desconocido",
                        type = detectDeviceType(device)
                    )
                )
            }

            BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                onDiscoveryFinished()
            }
        }
    }

    private fun detectDeviceType(device: BluetoothDevice): DeviceType {

        val name = device.name?.lowercase().orEmpty()

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

            else ->
                DeviceType.UNKNOWN
        }
    }
}