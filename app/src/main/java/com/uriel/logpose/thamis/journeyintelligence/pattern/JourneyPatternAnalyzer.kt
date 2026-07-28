package com.uriel.logpose.thamis.journeyintelligence.pattern

import com.uriel.logpose.thamis.journeyintelligence.model.JourneyPattern

/**
 * Detecta rutas y horarios habituales para anticipar necesidades.
 */
object JourneyPatternAnalyzer {
    private val history = mutableListOf<String>()

    fun detectPattern(routeId: String): JourneyPattern? {
        history.add(routeId)
        val frequency = history.count { it == routeId }
        
        return if (frequency > 3) {
            JourneyPattern(
                patternName = "Ruta Habitual",
                frequency = frequency,
                confidence = 0.8f
            )
        } else null
    }
}
