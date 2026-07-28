package com.uriel.logpose.core.telecom

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import com.uriel.logpose.core.compat.core.LogPoseLogger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Gestiona la robustez de la conexión SCO (Bluetooth Headset).
 * Implementa una máquina de estados para detectar degradación y desconexión.
 */
class ScoStateManager(
    private val context: Context,
    private val audioManager: AudioManager,
    private val onReconnectionRequest: () -> Unit
) {
    enum class ScoState { CONNECTED, DEGRADED, DISCONNECTED, RECONNECTING }

    private val _state = MutableStateFlow(ScoState.DISCONNECTED)
    val state = _state.asStateFlow()

    private var retryCount = 0
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var backoffJob: Job? = null

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED -> {
                    val audioState = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, -1)
                    handleAudioStateChanged(audioState)
                }
                AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED -> {
                    val scoState = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1)
                    handleScoAudioStateUpdated(scoState)
                }
            }
        }
    }

    fun startMonitoring() {
        val filter = IntentFilter().apply {
            addAction(BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED)
            addAction(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED)
        }
        context.registerReceiver(receiver, filter)
        updateCurrentState()
    }

    fun stopMonitoring() {
        try {
            context.unregisterReceiver(receiver)
        } catch (e: Exception) {}
        backoffJob?.cancel()
    }

    private fun handleAudioStateChanged(audioState: Int) {
        when (audioState) {
            BluetoothHeadset.STATE_AUDIO_CONNECTED -> {
                LogPoseLogger.i("SCO: Audio conectado (HFP).")
                _state.value = ScoState.CONNECTED
                retryCount = 0
            }
            BluetoothHeadset.STATE_AUDIO_DISCONNECTED -> {
                LogPoseLogger.w("SCO: Audio desconectado inesperadamente.")
                handleDisconnection()
            }
        }
    }

    private fun handleScoAudioStateUpdated(scoState: Int) {
        when (scoState) {
            AudioManager.SCO_AUDIO_STATE_CONNECTED -> {
                LogPoseLogger.d("SCO: Canal de audio establecido.")
                _state.value = ScoState.CONNECTED
            }
            AudioManager.SCO_AUDIO_STATE_DISCONNECTED -> {
                LogPoseLogger.d("SCO: Canal de audio cerrado.")
                if (_state.value == ScoState.CONNECTED) {
                    handleDisconnection()
                }
            }
            AudioManager.SCO_AUDIO_STATE_ERROR -> {
                LogPoseLogger.e("SCO: Error en el canal de audio.")
                _state.value = ScoState.DEGRADED
                handleDisconnection()
            }
        }
    }

    private fun handleDisconnection() {
        _state.value = ScoState.DISCONNECTED
        attemptReconnection()
    }

    private fun attemptReconnection() {
        if (backoffJob?.isActive == true) return

        backoffJob = scope.launch {
            _state.value = ScoState.RECONNECTING
            retryCount++
            // Backoff exponencial simple: 2s, 4s, 8s, 16s... máx 30s
            val delayMs = (Math.pow(2.0, retryCount.toDouble()) * 1000).toLong().coerceAtMost(30000)
            
            LogPoseLogger.i("SCO: Intentando reconexión en ${delayMs/1000}s (Intento $retryCount)...")
            delay(delayMs)
            
            onReconnectionRequest()
        }
    }

    private fun updateCurrentState() {
        val isScoOn = audioManager.isBluetoothScoOn
        _state.value = if (isScoOn) ScoState.CONNECTED else ScoState.DISCONNECTED
    }
}
