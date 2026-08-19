package com.uriel.logpose.thamis.journeyintelligence.context

import com.uriel.logpose.thamis.journeyintelligence.model.JourneyContext
import com.uriel.logpose.thamis.journeyintelligence.model.JourneyType

/**
 * Analiza la situación del viaje para asignarle una categoría semántica.
 */
object JourneyContextAnalyzer {

    fun analyze(distance: Double, time: Long, isFrequent: Boolean): JourneyContext {
        val type = when {
            isFrequent && distance < 20000 -> JourneyType.DAILY_COMMUTE
            distance > 100000 -> JourneyType.LONG_TRIP
            else -> JourneyType.UNKNOWN_ROUTE
        }

        return JourneyContext(
            type = type,
            objective = if (type == JourneyType.DAILY_COMMUTE) "Ir al trabajo" else "Explorar",
            currentState = "ACTIVE",
            userNeeds = if (type == JourneyType.LONG_TRIP) listOf("GAS", "WEATHER_UPDATE") else emptyList()
        )
    }
}
