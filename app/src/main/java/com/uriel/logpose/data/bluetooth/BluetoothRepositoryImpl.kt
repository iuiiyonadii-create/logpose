package com.uriel.logpose.data.bluetooth

import android.content.Context
import com.uriel.logpose.core.bluetooth.BluetoothDeviceMapper
import com.uriel.logpose.core.bluetooth.BluetoothManager
import com.uriel.logpose.domain.models.BluetoothState as DomainBluetoothState
import com.uriel.logpose.domain.models.BluetoothStatus
import com.uriel.logpose.domain.models.LogPoseDevice
import com.uriel.logpose.domain.repositories.BluetoothRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.uriel.logpose.core.bluetooth.BluetoothState as CoreBluetoothState

class BluetoothRepositoryImpl(
    private val context: Context
) : BluetoothRepository {

    private val bluetoothManager = BluetoothManager(context)
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _connectionState = MutableStateFlow(BluetoothStatus(DomainBluetoothState.DISCONNECTED))
    override val connectionState: StateFlow<BluetoothStatus> = _connectionState.asStateFlow()

    private val _isEnabled = MutableStateFlow(bluetoothManager.isBluetoothEnabled())
    override val isEnabled: StateFlow<Boolean> = _isEnabled.asStateFlow()

    private val _discoveredDevices = MutableStateFlow<List<LogPoseDevice>>(emptyList())
    override val discoveredDevices: StateFlow<List<LogPoseDevice>> = _discoveredDevices.asStateFlow()

    init {
        scope.launch {
            bluetoothManager.connectionState.collectLatest { coreState ->
                _connectionState.value = BluetoothStatus(mapCoreToDomainState(coreState))
            }
        }
        scope.launch {
            bluetoothManager.isEnabled.collectLatest { enabled ->
                _isEnabled.value = enabled
            }
        }
        scope.launch {
            bluetoothManager.discoveredDevices.collectLatest { devices ->
                _discoveredDevices.value = devices.map { BluetoothDeviceMapper.mapToDomain(it) }
            }
        }
    }

    override fun startScan() {
        bluetoothManager.startDiscovery()
    }

    override fun stopScan() {
        bluetoothManager.cancelDiscovery()
    }

    override suspend fun connect(device: LogPoseDevice): Boolean {
        val adapter = android.bluetooth.BluetoothAdapter.getDefaultAdapter()
        val bluetoothDevice = adapter.getRemoteDevice(device.mac)
        return bluetoothManager.connect(bluetoothDevice)
    }

    override fun disconnect() {
        bluetoothManager.disconnect()
    }

    override fun getPairedDevices(): List<LogPoseDevice> {
        return bluetoothManager.getPairedDevices().map { BluetoothDeviceMapper.mapToDomain(it) }
    }

    override fun hasRequiredPermissions(): Boolean {
        return true
    }

    // UI compatibility methods
    override fun isBluetoothEnabled(): Boolean = bluetoothManager.isBluetoothEnabled()
    override fun saveSelectedDevice(mac: String) {}
    override fun getSelectedDeviceMac(): String? = null
    override fun getSavedDevice(): LogPoseDevice? = null
    override fun startDiscovery(onDeviceFound: (LogPoseDevice) -> Unit, onFinished: () -> Unit) {
        bluetoothManager.startDiscovery()
    }
    override suspend fun connectDevice(device: LogPoseDevice): Boolean = connect(device)
    override fun disconnectDevice() = disconnect()
    override fun isConnected(): Boolean = connectionState.value.state == DomainBluetoothState.CONNECTED
    override fun releaseReceiver() {}
    override fun registerBluetoothState() {}
    override fun getDeviceBatteryLevel(mac: String): Int? = null
    override fun hasPermission(): Boolean = hasRequiredPermissions()

    private fun mapCoreToDomainState(coreState: CoreBluetoothState): DomainBluetoothState {
        return when (coreState) {
            CoreBluetoothState.IDLE -> DomainBluetoothState.DISCONNECTED
            CoreBluetoothState.DISCONNECTED -> DomainBluetoothState.DISCONNECTED
            CoreBluetoothState.SCANNING -> DomainBluetoothState.CONNECTING
            CoreBluetoothState.CONNECTING -> DomainBluetoothState.CONNECTING
            CoreBluetoothState.CONNECTED -> DomainBluetoothState.CONNECTED
            CoreBluetoothState.FAILED -> DomainBluetoothState.ERROR
        }
    }
}
