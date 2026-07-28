package com.uriel.logpose.core.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Handles Bluetooth device discovery and filtering.
 */
class BluetoothScanner(private val context: Context) {

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val _discoveredDevices = MutableStateFlow<Set<BluetoothDevice>>(emptySet())
    val discoveredDevices = _discoveredDevices.asStateFlow()

    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        // Filter for relevant devices (Headsets, Handsfree)
                        _discoveredDevices.value += it
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startScan(): Boolean {
        if (bluetoothAdapter?.isDiscovering == true) {
            bluetoothAdapter.cancelDiscovery()
        }
        _discoveredDevices.value = emptySet()
        context.registerReceiver(receiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
        return bluetoothAdapter?.startDiscovery() ?: false
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        if (bluetoothAdapter?.isDiscovering == true) {
            bluetoothAdapter.cancelDiscovery()
        }
        try {
            context.unregisterReceiver(receiver)
        } catch (e: Exception) {
            // Already unregistered
        }
    }

    fun addDevice(device: BluetoothDevice) {
        _discoveredDevices.value += device
    }

    fun clear() {
        _discoveredDevices.value = emptySet()
    }
}
