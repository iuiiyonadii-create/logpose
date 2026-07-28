package com.uriel.logpose.core.services

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.telecom.TelecomManager
import androidx.core.content.ContextCompat
import com.uriel.logpose.core.telecom.LogPoseTelecom
import com.uriel.logpose.core.compat.core.LogPoseLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TripSessionManager(private val context: Context) {

    private var currentConnection: LogPoseConnection? = null
    private val _state = MutableStateFlow(TripSessionState.IDLE)
    val state: StateFlow<TripSessionState> = _state.asStateFlow()

    fun startTrip() {
        if (_state.value != TripSessionState.IDLE) {
            LogPoseLogger.d("TripSession: Ya hay un viaje en curso o iniciando.")
            return
        }
        
        _state.value = TripSessionState.STARTING
        LogPoseLogger.i("TripSession: Iniciando viaje (Session delegada a LogPoseCallService).")

        // No lanzamos placeCall aquí para evitar duplicidad con LogPoseCallService
        // La conexión se activará cuando la sesión de Telecom sea confirmada externamente.
    }

    fun endTrip() {
        LogPoseLogger.i("TripSession: Finalizando viaje.")
        _state.value = TripSessionState.ENDING
        currentConnection?.onDisconnect()
        currentConnection = null
        _state.value = TripSessionState.IDLE
    }

    internal fun onSessionActive(connection: LogPoseConnection) {
        this.currentConnection = connection
        _state.value = TripSessionState.ACTIVE
        LogPoseLogger.i("TripSession: SESIÓN ACTIVA. Casco anclado.")
    }

    internal fun onHeadsetDropped() {
        if (_state.value == TripSessionState.ACTIVE) {
            _state.value = TripSessionState.RECONNECTING
            LogPoseLogger.w("TripSession: Se perdió el casco. Esperando reconexión...")
        }
    }

    internal fun onHeadsetRecovered() {
        if (_state.value == TripSessionState.RECONNECTING) {
            _state.value = TripSessionState.ACTIVE
            LogPoseLogger.i("TripSession: Casco recuperado.")
        }
    }
}
