package com.uriel.logpose.thamis.beta.session

import com.uriel.logpose.thamis.beta.model.DrivingScenario
import com.uriel.logpose.thamis.beta.model.DrivingSession

/**
 * Gestiona el ciclo de vida de las sesiones de prueba beta.
 */
object DrivingSessionManager {
    private var activeSession: DrivingSession? = null
    private val sessionHistory = mutableListOf<DrivingSession>()

    fun startSession(scenario: DrivingScenario) {
        activeSession = DrivingSession(scenario = scenario)
    }

    fun endSession(summary: String) {
        activeSession?.let {
            it.endTime = System.currentTimeMillis()
            it.resultSummary = summary
            sessionHistory.add(it)
        }
        activeSession = null
    }

    fun recordEvent(event: String) {
        activeSession?.events?.add(event)
    }

    fun getActiveSession(): DrivingSession? = activeSession
}
