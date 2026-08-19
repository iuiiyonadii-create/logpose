package com.uriel.logpose.features.weather

data class LocationCoordinates(
    val latitude: Double,
    val longitude: Double
)

object LocationResolver {

    /**
     * Resuelve las coordenadas actuales para la consulta de clima.
     * Default: Buenos Aires (-34.6037, -58.3816) si no hay GPS disponible.
     */
    fun resolveLocation(): LocationCoordinates {
        return LocationCoordinates(
            latitude = -34.6037,
            longitude = -58.3816
        )
    }
}
