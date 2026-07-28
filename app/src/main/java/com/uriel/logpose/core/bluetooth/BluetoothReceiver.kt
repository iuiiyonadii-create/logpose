package com.uriel.logpose.core.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

/**
 * Receptor de eventos Bluetooth (Estado, Descubrimiento y Conexión).
 */
class BluetoothReceiver(private val bluetoothManager: BluetoothManager) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            BluetoothAdapter.ACTION_STATE_CHANGED -> {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                val isEnabled = state == BluetoothAdapter.STATE_ON
                bluetoothManager.onBluetoothStateChanged(isEnabled)
            }
            BluetoothDevice.ACTION_FOUND -> {
                @Suppress("DEPRECATION")
                val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                device?.let { bluetoothManager.addDiscoveredDevice(it) }
            }
            BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                bluetoothManager.updateState(BluetoothState.SCANNING)
                bluetoothManager.clearDiscoveredDevices()
            }
            BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                bluetoothManager.updateState(BluetoothState.IDLE)
            }
            BluetoothDevice.ACTION_ACL_CONNECTED -> {
                bluetoothManager.updateState(BluetoothState.CONNECTED)
            }
            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                bluetoothManager.updateState(BluetoothState.DISCONNECTED)
            }
        }
    }

    fun getIntentFilter(): IntentFilter {
        return IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        }
    }
}
