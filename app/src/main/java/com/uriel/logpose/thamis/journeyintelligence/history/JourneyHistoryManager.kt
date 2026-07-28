package com.uriel.logpose.thamis.journeyintelligence.history

import com.uriel.logpose.thamis.journeyintelligence.model.JourneySession

/**
 * Gestor persistente (en sesión) de los viajes inteligentes.
 */
object JourneyHistoryManager {
    private val history = mutableListOf<JourneySession>()
    private var isRecording = true

    fun recordSession(session: JourneySession) {
        if (!isRecording) return
        history.add(session)
        if (history.size > 100) history.removeAt(0)
    }

    fun setRecording(enabled: Boolean) {
        isRecording = enabled
    }

    fun clear() = history.clear()

    fun getAll(): List<JourneySession> = history.toList()
}
