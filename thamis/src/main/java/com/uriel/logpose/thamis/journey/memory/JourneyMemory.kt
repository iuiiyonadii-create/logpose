package com.uriel.logpose.thamis.journey.memory

import com.uriel.logpose.thamis.journey.model.JourneySession

/**
 * Memoria persistente (en sesión) de los viajes realizados.
 */
object JourneyMemory {
    
    private var lastJourney: JourneySession? = null
    private var totalDistance: Double = 0.0
    private var totalTimeMs: Long = 0L

    /**
     * Registra el cierre de un viaje en la memoria.
     */
    fun registerJourney(session: JourneySession) {
        lastJourney = session
        totalDistance += session.distanceMeters
        session.endTime?.let { end ->
            totalTimeMs += (end - session.startTime)
        }
    }

    /**
     * Devuelve los datos del último viaje realizado.
     */
    fun getLastJourneySummary(): JourneySession? = lastJourney

    fun getTotalDistance(): Double = totalDistance
    
    fun getTotalTimeMs(): Long = totalTimeMs
}
