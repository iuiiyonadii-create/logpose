package com.uriel.logpose.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import com.uriel.logpose.core.DeviceClassifier
import com.uriel.logpose.engine.LogPoseEngine
import com.uriel.logpose.model.LogPoseDevice

class BluetoothReceiver(
    private val onDeviceFound: (LogPoseDevice) -> Unit,
    private val onDiscoveryFinished: () -> Unit,
    private val onDeviceConnected: ((LogPoseDevice) -> Unit)? = null,
    private val onDeviceDisconnected: ((LogPoseDevice) -> Unit)? = null
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

                onDeviceFound(device.toLogPoseDevice(false))
            }

            BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                onDiscoveryFinished()
            }

            BluetoothDevice.ACTION_ACL_CONNECTED -> {

                val device = intent.getParcelableExtra<BluetoothDevice>(
                    BluetoothDevice.EXTRA_DEVICE
                ) ?: return

                val logPoseDevice = device.toLogPoseDevice(true)

                LogPoseEngine.onBluetoothConnected(logPoseDevice)

                onDeviceConnected?.invoke(logPoseDevice)
            }

            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {

                val device = intent.getParcelableExtra<BluetoothDevice>(
                    BluetoothDevice.EXTRA_DEVICE
                ) ?: return

                val logPoseDevice = device.toLogPoseDevice(false)

                LogPoseEngine.onBluetoothDisconnected(logPoseDevice)

                onDeviceDisconnected?.invoke(logPoseDevice)
            }

        }

    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun BluetoothDevice.toLogPoseDevice(
        connected: Boolean
    ): LogPoseDevice {

        return LogPoseDevice(
            mac = address,
            name = name ?: "Desconocido",
            type = DeviceClassifier.detect(
                name.orEmpty()
            ),
            connected = connected
        )

    }

}