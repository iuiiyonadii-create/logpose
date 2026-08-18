package com.uriel.logpose.data.bluetooth

import android.content.Context
import android.bluetooth.BluetoothManager as AndroidBluetoothManager
import com.uriel.logpose.features.bluetooth.BluetoothDeviceMapper
import com.uriel.logpose.features.bluetooth.BluetoothManager
import com.uriel.logpose.domain.models.BluetoothState as DomainBluetoothState
import com.uriel.logpose.domain.models.BluetoothStatus
import com.uriel.logpose.domain.models.LogPoseDevice
import com.uriel.logpose.domain.repositories.BluetoothRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class BluetoothRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val bluetoothManager: BluetoothManager
) : BluetoothRepository {

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _connectionState = MutableStateFlow(BluetoothStatus(DomainBluetoothState.DISCONNECTED))
    override val connectionState: StateFlow<BluetoothStatus> = _connectionState.asStateFlow()

    private val _isEnabled = MutableStateFlow(false)
    override val isEnabled: StateFlow<Boolean> = _isEnabled.asStateFlow()

    private val _discoveredDevices = MutableStateFlow<List<LogPoseDevice>>(emptyList())
    override val discoveredDevices: StateFlow<List<LogPoseDevice>> = _discoveredDevices.asStateFlow()

    init {
        scope.launch {
            _isEnabled.value = isBluetoothEnabled()
            
            // Note: BluetoothManager in features currently uses different enum names or logic.
            // Bridging to DomainBluetoothState based on current implementation.
            bluetoothManager.connectionState.collectLatest { coreState ->
                // Features.BluetoothManager uses ConnectionState enum
                _connectionState.value = BluetoothStatus(DomainBluetoothState.CONNECTED) 
            }
        }
    }

    override fun startScan() {
        // Not implemented in features.BluetoothManager
    }

    override fun stopScan() {
        // Not implemented in features.BluetoothManager
    }

    override suspend fun connect(device: LogPoseDevice): Boolean {
        bluetoothManager.connectToDevice(device.mac)
        return isConnected()
    }

    override fun disconnect() {
        bluetoothManager.disconnect()
    }

    override fun getPairedDevices(): List<LogPoseDevice> {
        if (!hasPermission()) return emptyList()
        val btManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as AndroidBluetoothManager
        return btManager.adapter?.bondedDevices?.map { BluetoothDeviceMapper.mapToDomain(it) } ?: emptyList()
    }

    override fun hasRequiredPermissions(): Boolean {
        return com.uriel.logpose.core.compat.PermissionManager.hasBluetoothPermission(context)
    }

    override fun isBluetoothEnabled(): Boolean {
        val btManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as AndroidBluetoothManager
        return btManager.adapter?.isEnabled ?: false
    }

    override fun saveSelectedDevice(mac: String) {}
    override fun getSelectedDeviceMac(): String? = null
    override fun getSavedDevice(): LogPoseDevice? = null
    override fun startDiscovery(onDeviceFound: (LogPoseDevice) -> Unit, onFinished: () -> Unit) {
        // Not implemented in features.BluetoothManager
    }
    override suspend fun connectDevice(device: LogPoseDevice): Boolean = connect(device)
    override fun disconnectDevice() = disconnect()
    override fun isConnected(): Boolean = connectionState.value.state == DomainBluetoothState.CONNECTED
    override fun releaseReceiver() {}
    override fun registerBluetoothState() {}
    override fun getDeviceBatteryLevel(mac: String): Int? = null
    override fun hasPermission(): Boolean = hasRequiredPermissions()
}
