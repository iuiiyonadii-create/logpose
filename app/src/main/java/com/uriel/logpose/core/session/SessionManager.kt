package com.uriel.logpose.core.session

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.features.music.MusicManager
import com.uriel.logpose.features.voice.VoiceManager
import com.uriel.logpose.features.voice.FeedbackManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class SessionState {
    BLUETOOTH_DISCONNECTED,
    WAITING_RECONNECTION,
    SESSION_ACTIVE,
    SESSION_FINISHED
}

object SessionManager {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var reconnectionJob: Job? = null
    
    private val _state = MutableStateFlow(SessionState.BLUETOOTH_DISCONNECTED)
    val state: StateFlow<SessionState> = _state.asStateFlow()

    // Tiempo de gracia predeterminado (Sector 8.2)
    private var gracePeriodMs: Long = 5000 

    fun onBluetoothConnected(deviceName: String) {
        reconnectionJob?.cancel()
        
        if (_state.value == SessionState.WAITING_RECONNECTION) {
            LogPoseLogger.i("SessionManager: Reconexión rápida detectada.")
            _state.value = SessionState.SESSION_ACTIVE
            return
        }

        LogPoseLogger.i("SessionManager: Iniciando sesión con $deviceName")
        _state.value = SessionState.SESSION_ACTIVE
        
        // El casco inicia la experiencia (Sector 8.1)
        com.uriel.logpose.thamis.thamis_final.ThamisCore.getInstance(com.uriel.logpose.core.app.LogPoseApplication.instance).ready()
        FeedbackManager.speak("Listo.")
    }

    fun onBluetoothDisconnected() {
        if (_state.value != SessionState.SESSION_ACTIVE) return

        LogPoseLogger.i("SessionManager: Bluetooth perdido. Esperando reconexión...")
        _state.value = SessionState.WAITING_RECONNECTION
        
        reconnectionJob = scope.launch {
            delay(gracePeriodMs)
            if (_state.value == SessionState.WAITING_RECONNECTION) {
                finishSession()
            }
        }
    }

    private fun finishSession() {
        LogPoseLogger.i("SessionManager: Sesión finalizada automáticamente.")
        _state.value = SessionState.SESSION_FINISHED
        
        // Limpieza de servicios (Sector 8.2)
        MusicManager.pause()
        VoiceManager.stop()
        com.uriel.logpose.thamis.thamis_final.ThamisCore.getInstance(com.uriel.logpose.core.app.LogPoseApplication.instance).shutdown()
        
        _state.value = SessionState.BLUETOOTH_DISCONNECTED
    }
}
