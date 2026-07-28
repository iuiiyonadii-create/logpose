package com.uriel.logpose.thamis.navigation.location

/**
 * Suite de pruebas de estrés para validar el motor de ubicación bajo diferentes condiciones de sensor.
 */
class LocationStressTest {
    fun run() {
        val tests = listOf(
            "¿Dónde estoy?",
            "Qué ciudad es esta",
            "Estacion de servicio",
            "Policia cerca",
            "Hospital urgente"
        )

        val locations = listOf(
            CurrentLocation(gpsAccuracy = 5f, city = "Buenos Aires", street = "9 de Julio"),
            CurrentLocation(gpsAccuracy = 500f),
            CurrentLocation(gpsAccuracy = 20f, city = "Desconocida")
        )

        tests.forEach { text ->
            locations.forEach { loc ->
                LocationShadowController.process(text, loc, true, true)
            }
        }
    }
}
