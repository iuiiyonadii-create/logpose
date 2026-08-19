package com.uriel.logpose.thamis.navigation.location

/**
 * Genera respuestas en lenguaje natural basadas en el contexto de ubicación.
 */
object LocationFormatter {
    fun formatResponse(intent: LocationIntent, location: CurrentLocation, gpsAvailable: Boolean): String {
        if (!gpsAvailable) return "No tengo señal de GPS en este momento."
        if (location.gpsAccuracy > 100f) return "La precisión del GPS es muy baja para darte una ubicación exacta."

        return when (intent) {
            LocationIntent.WHERE_AM_I -> buildWhereAmI(location)
            LocationIntent.WHAT_CITY -> location.city?.let { "Estás en $it." } ?: "No reconozco la ciudad actual."
            LocationIntent.WHAT_STREET -> location.street?.let { "Estás sobre $it." } ?: "No reconozco el nombre de la calle."
            LocationIntent.NEAREST_GAS -> "Buscando la estación de servicio más cercana."
            LocationIntent.NEAREST_PARKING -> "Buscando estacionamiento para la moto."
            LocationIntent.NEAREST_HOSPITAL -> "Buscando el centro médico más cercano."
            LocationIntent.NEAREST_POLICE -> "Buscando la comisaría más cercana."
            LocationIntent.UNKNOWN -> "No entendí tu consulta sobre la ubicación."
        }
    }

    private fun buildWhereAmI(loc: CurrentLocation): String {
        return if (loc.street != null && loc.city != null) {
            "Estás en ${loc.street}, en la ciudad de ${loc.city}."
        } else if (loc.city != null) {
            "Estás en ${loc.city}."
        } else {
            "No pude determinar tu ubicación exacta, pero tengo tus coordenadas."
        }
    }
}
