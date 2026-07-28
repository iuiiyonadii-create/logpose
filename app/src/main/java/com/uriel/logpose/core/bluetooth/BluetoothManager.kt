package com.uriel.logpose.core.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import com.uriel.logpose.core.permissions.BluetoothPermissionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Orchestrator for all Bluetooth-related activities.
 */
class BluetoothManager(private val context: Context) {

    private val adapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val scanner = BluetoothScanner(context)
    private val connectionManager = BluetoothConnectionManager(context)
    private val permissionManager = BluetoothPermissionManager(context)

    val connectionState: StateFlow<BluetoothState> = connectionManager.connectionState
    val discoveredDevices: StateFlow<Set<BluetoothDevice>> = scanner.discoveredDevices

    private val _isEnabled = MutableStateFlow(adapter?.isEnabled ?: false)
    val isEnabled = _isEnabled.asStateFlow()

    fun isBluetoothEnabled(): Boolean = adapter?.isEnabled ?: false

    fun startDiscovery(): Boolean {
        if (!permissionManager.hasRequiredPermissions()) return false
        return scanner.startScan()
    }

    fun cancelDiscovery() {
        scanner.stopScan()
    }

    fun connect(device: BluetoothDevice): Boolean {
        return connectionManager.connect(device)
    }

    fun disconnect() {
        connectionManager.disconnect()
    }

    fun onBluetoothStateChanged(enabled: Boolean) {
        _isEnabled.value = enabled
    }

    fun addDiscoveredDevice(device: BluetoothDevice) {
        scanner.addDevice(device)
    }

    fun clearDiscoveredDevices() {
        scanner.clear()
    }

    fun getPairedDevices(): List<BluetoothDevice> {
        return adapter?.bondedDevices?.toList() ?: emptyList()
    }

    fun updateState(state: BluetoothState) {
        // Method for manual state updates if needed
    }
}
