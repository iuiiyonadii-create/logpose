package com.uriel.logpose.features.communication

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * FASE 26.5 — LOGPOSE COMMUNICATION INTELLIGENCE
 * FASE 2: CALL MANAGEMENT SYSTEM
 */
object CallManager {

    enum class CallState { IDLE, INCOMING, ACTIVE, ENDED }

    private var currentState = CallState.IDLE

    fun handleIncomingCall(contact: String) {
        currentState = CallState.INCOMING
        LogPoseLogger.i("CallManager: Llamada entrante de $contact")
    }

    fun acceptCall() {
        if (currentState == CallState.INCOMING) {
            currentState = CallState.ACTIVE
            LogPoseLogger.i("CallManager: Llamada aceptada")
        }
    }

    fun endCall() {
        currentState = CallState.ENDED
        LogPoseLogger.i("CallManager: Llamada finalizada")
        currentState = CallState.IDLE
    }
}
