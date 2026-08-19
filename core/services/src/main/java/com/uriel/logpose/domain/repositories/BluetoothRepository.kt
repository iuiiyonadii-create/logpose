package com.uriel.logpose.domain.repositories

import com.uriel.logpose.domain.models.BluetoothStatus
import com.uriel.logpose.domain.models.LogPoseDevice
import kotlinx.coroutines.flow.StateFlow

/**
 * Unified Contract for Bluetooth operations.
 */
interface BluetoothRepository {
    val connectionState: StateFlow<BluetoothStatus>
    val discoveredDevices: StateFlow<List<LogPoseDevice>>
    val isEnabled: StateFlow<Boolean>

    fun startScan()
    fun stopScan()
    suspend fun connect(device: LogPoseDevice): Boolean
    fun disconnect()
    fun getPairedDevices(): List<LogPoseDevice>
    fun hasRequiredPermissions(): Boolean
    
    // UI specific methods
    fun isBluetoothEnabled(): Boolean
    fun saveSelectedDevice(mac: String)
    fun getSelectedDeviceMac(): String?
    fun getSavedDevice(): LogPoseDevice?
    fun startDiscovery(onDeviceFound: (LogPoseDevice) -> Unit, onFinished: () -> Unit)
    suspend fun connectDevice(device: LogPoseDevice): Boolean
    fun disconnectDevice()
    fun isConnected(): Boolean
    fun releaseReceiver()
    fun registerBluetoothState()
    fun getDeviceBatteryLevel(mac: String): Int?
    fun hasPermission(): Boolean
}
