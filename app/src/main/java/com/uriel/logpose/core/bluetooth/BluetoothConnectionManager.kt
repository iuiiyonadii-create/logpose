package com.uriel.logpose.core.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages the connection lifecycle of specific Bluetooth profiles (HFP/A2DP).
 */
class BluetoothConnectionManager(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var reconnectionJob: Job? = null
    private var lastAttemptedDevice: BluetoothDevice? = null

    private var bluetoothHeadset: BluetoothHeadset? = null
    private val _connectionState = MutableStateFlow(BluetoothState.DISCONNECTED)
    val connectionState = _connectionState.asStateFlow()

    private val profileListener = object : BluetoothProfile.ServiceListener {
        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile?) {
            if (profile == BluetoothProfile.HEADSET) {
                bluetoothHeadset = proxy as BluetoothHeadset
                Log.d("BT_CONN", "Headset profile connected")
            }
        }

        override fun onServiceDisconnected(profile: Int) {
            if (profile == BluetoothProfile.HEADSET) {
                bluetoothHeadset = null
                _connectionState.value = BluetoothState.DISCONNECTED
            }
        }
    }

    init {
        val adapter = android.bluetooth.BluetoothAdapter.getDefaultAdapter()
        adapter?.getProfileProxy(context, profileListener, BluetoothProfile.HEADSET)
    }

    @SuppressLint("MissingPermission")
    fun connect(device: BluetoothDevice): Boolean {
        lastAttemptedDevice = device
        _connectionState.value = BluetoothState.CONNECTING
        
        return if (bluetoothHeadset?.getConnectionState(device) == BluetoothProfile.STATE_CONNECTED) {
            _connectionState.value = BluetoothState.CONNECTED
            true
        } else {
            startReconnectionTimer()
            true // En MVP devolvemos true mientras simula
        }
    }

    private fun startReconnectionTimer() {
        reconnectionJob?.cancel()
        reconnectionJob = scope.launch {
            while (isActive && _connectionState.value != BluetoothState.CONNECTED) {
                delay(5000)
                Log.d("BT_CONN", "Reintentando conexión automática...")
                // Aquí iría el intento real de conexión por Profile
            }
        }
    }

    fun disconnect() {
        reconnectionJob?.cancel()
        _connectionState.value = BluetoothState.DISCONNECTED
    }

    fun isConnected(): Boolean = _connectionState.value == BluetoothState.CONNECTED
}
