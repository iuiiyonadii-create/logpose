package com.uriel.logpose.thamis.navigation

import com.uriel.logpose.thamis.navigation.model.NavigationGoal

/**
 * Clasifica el texto crudo en una intención de ruta estructurada.
 */
object RouteIntentClassifier {

    enum class RouteIntent {
        GO_HOME,
        GO_WORK,
        GO_CONTACT,
        GO_PLACE,
        OPEN_MAP,
        CONTINUE_ROUTE,
        STOP_ROUTE,
        SEARCH_GAS_STATION,
        SEARCH_PARKING,
        SEARCH_CHARGER,
        UNKNOWN
    }

    fun classify(text: String): RouteIntent {
        val lowerText = text.lowercase().trim()
        
        return when {
            lowerText.contains("casa") -> RouteIntent.GO_HOME
            lowerText.contains("trabajo") -> RouteIntent.GO_WORK
            lowerText.contains("juan") -> RouteIntent.GO_CONTACT
            lowerText.contains("mapa") || lowerText.contains("maps") -> RouteIntent.OPEN_MAP
            lowerText.contains("para") || lowerText.contains("cancela") || lowerText.contains("detener") -> RouteIntent.STOP_ROUTE
            lowerText.contains("estacion") || lowerText.contains("nafta") || lowerText.contains("gasolinera") -> RouteIntent.SEARCH_GAS_STATION
            else -> RouteIntent.UNKNOWN
        }
    }
}
