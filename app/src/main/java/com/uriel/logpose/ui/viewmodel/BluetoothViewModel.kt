package com.uriel.logpose.ui.viewmodel

import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uriel.logpose.core.services.LogPoseCallService
import com.uriel.logpose.domain.repositories.BluetoothRepository
import com.uriel.logpose.domain.models.LogPoseDevice
import com.uriel.logpose.features.voice.VoiceManager
import com.uriel.logpose.thamis.thamis_final.ThamisCore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.os.BatteryManager
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val repository: BluetoothRepository,
    private val thamisCore: ThamisCore
) : ViewModel() {

    private val _state = MutableStateFlow(BluetoothUiState())
    val state = _state.asStateFlow()

    private var registeredContext: Context? = null

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "android.bluetooth.device.action.BATTERY_LEVEL_CHANGED" -> {
                    val level = intent.getIntExtra("android.bluetooth.device.extra.BATTERY_LEVEL", -1)
                    if (level != -1) {
                        _state.update { it.copy(deviceBattery = level) }
                    }
                }
                Intent.ACTION_BATTERY_CHANGED -> {
                    val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                    val batteryPct = (level * 100 / scale.toFloat()).toInt()
                    _state.update { it.copy(phoneBattery = batteryPct) }
                }
            }
        }
    }

    fun registerBatteryReceiver(context: Context) {
        if (registeredContext == null) {
            val filter = IntentFilter().apply {
                addAction("android.bluetooth.device.action.BATTERY_LEVEL_CHANGED")
                addAction(Intent.ACTION_BATTERY_CHANGED)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.applicationContext.registerReceiver(batteryReceiver, filter, Context.RECEIVER_EXPORTED)
            } else {
                context.applicationContext.registerReceiver(batteryReceiver, filter)
            }
            registeredContext = context.applicationContext
            
            val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            val level = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            _state.update { it.copy(phoneBattery = level) }
        }
    }

    fun unregisterBatteryReceiver() {
        registeredContext?.let {
            try {
                it.unregisterReceiver(batteryReceiver)
            } catch (e: Exception) {}
            registeredContext = null
        }
    }

    override fun onCleared() {
        super.onCleared()
        unregisterBatteryReceiver()
    }

    init {
        repository.registerBluetoothState()
        observeBluetoothState()
        observeEngineState()
        observeDiscoveredDevices()
        refresh()
    }

    private fun observeDiscoveredDevices() {
        viewModelScope.launch {
            repository.discoveredDevices.collectLatest { devices ->
                _state.update { it.copy(discoveredDevices = devices) }
            }
        }
    }

    private fun observeEngineState() {
        viewModelScope.launch {
            // SINCRO CLAUDE: Observamos el estado global del servicio.
            // Si el servicio no está corriendo físicamente, forzamos false.
            LogPoseCallService.isServiceRunning.collectLatest { running ->
                _state.update { it.copy(serviceRunning = running) }
            }
        }

        viewModelScope.launch {
            thamisCore.state.collectLatest { engineState ->
                // SINCRO CLAUDE: Si el motor de Thamis está detenido, el botón DEBE estar en verde (Iniciar)
                if (engineState == com.uriel.logpose.core.compat.core.AppState.STOPPED) {
                    _state.update { it.copy(serviceRunning = false) }
                }
            }
        }
    }

    private fun observeBluetoothState() {
        viewModelScope.launch {
            repository.isEnabled.collectLatest { enabled ->
                _state.update { it.copy(bluetoothEnabled = enabled) }
            }
        }
    }

    fun refresh(){
        viewModelScope.launch {
            if (!repository.hasPermission()) {
                Log.w("LOGPOSE_BT", "BluetoothViewModel: Postponing refresh due to lack of permissions")
                _state.update { it.copy(loading = false) }
                return@launch
            }

            val isEnabled = repository.isBluetoothEnabled()
            val pairedDevices = repository.getPairedDevices()
            val savedDevice = repository.getSavedDevice()
            
            val activeDevice = pairedDevices.find { it.connected } ?: savedDevice
            val batteryLevel = activeDevice?.let { repository.getDeviceBatteryLevel(it.mac) }

            _state.update {
                it.copy(
                    bluetoothEnabled = isEnabled,
                    devices = pairedDevices,
                    savedDevice = savedDevice,
                    selectedDevice = activeDevice,
                    deviceBattery = batteryLevel ?: it.deviceBattery,
                    loading = false
                )
            }
        }
    }

    fun selectDevice(device: LogPoseDevice){
        _state.update { it.copy(selectedDevice = device, error = null) }
    }

    fun saveDevice(){
        val device = _state.value.selectedDevice ?: return
        repository.saveSelectedDevice(device.mac)
        _state.update { it.copy(savedDevice = device, selectedDevice = device, error = null) }
    }

    fun startDiscovery(){
        if (!_state.value.bluetoothEnabled) {
            _state.update { it.copy(error = "El Bluetooth está apagado.") }
            return
        }

        _state.update { it.copy(discovering = true, error = null) }
        repository.startDiscovery(
            onDeviceFound = { }, // Se maneja vía StateFlow
            onFinished = { _state.update { it.copy(discovering = false) } }
        )
    }

    fun connect(){
        val device = _state.value.selectedDevice ?: return
        connectDevice(device)
    }

    fun startLogPose(context: android.content.Context){
        val device = _state.value.savedDevice ?: _state.value.selectedDevice
        if(device == null){
            _state.update { it.copy(error = "No hay dispositivo seleccionado.") }
            return
        }
        connectDevice(device, context)
    }

    private fun connectDevice(device: LogPoseDevice, context: android.content.Context? = null){
        viewModelScope.launch {
            val result = repository.connectDevice(device)
            if(result){
                _state.update { it.copy(serviceRunning = true, selectedDevice = device, savedDevice = device, error = null) }
                
                if (context != null) {
                    thamisCore.onBluetoothConnected(device)
                    val intent = Intent(context, LogPoseCallService::class.java).apply {
                        action = LogPoseCallService.ACTION_START_TRIP
                    }
                    context.startForegroundService(intent)
                }
                VoiceManager.start()
            } else {
                _state.update { it.copy(serviceRunning = false, error = "Bluetooth no conectado.") }
            }
        }
    }

    fun startLogPoseDebug(context: android.content.Context) {
        _state.update { it.copy(serviceRunning = true, error = "Modo pruebas.") }
        thamisCore.ready()
        val intent = Intent(context, LogPoseCallService::class.java).apply {
            action = LogPoseCallService.ACTION_START_TRIP
        }
        context.startForegroundService(intent)
        VoiceManager.start()
    }

    fun stopLogPose(context: android.content.Context){
        repository.disconnectDevice()
        val intent = Intent(context, LogPoseCallService::class.java).apply {
            action = LogPoseCallService.ACTION_END_TRIP
        }
        context.startForegroundService(intent) // Usamos startForegroundService para endTrip también por consistencia
        thamisCore.shutdown()
        _state.update { it.copy(serviceRunning = false) }
    }
}
