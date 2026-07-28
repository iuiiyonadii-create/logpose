package com.uriel.logpose.features.hardware

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.domain.ConnectionState

/**
 * FASE 26.8 — LOGPOSE BLUETOOTH & HARDWARE ECOSYSTEM
 * FASE 1: HARDWARE MANAGER CORE
 */
object HardwareManager {

    private var intercomState = ConnectionState.DISCONNECTED

    fun updateIntercomState(state: ConnectionState) {
        intercomState = state
        LogPoseLogger.i("HardwareManager: Estado del intercomunicador: $state")
    }

    fun isHardwareReady(): Boolean = intercomState == ConnectionState.CONNECTED
}
