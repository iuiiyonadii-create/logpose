package com.uriel.logpose.thamis.lab.manual

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.lab.events.RealWorldEventGenerator
import com.uriel.logpose.thamis.lab.model.SimulationScenario
import com.uriel.logpose.thamis.lab.simulation.DeviceSimulationEngine

/**
 * Consola para la creación manual de escenarios de prueba por parte del desarrollador.
 */
object ManualTestConsole {

    fun runCityNightScenario() {
        val scenario = SimulationScenario(
            name = "Moto Ciudad Noche",
            description = "Simulación urbana con ruido y múltiples eventos.",
            eventSequence = listOf(
                RealWorldEventGenerator.createIncomingCall(),
                RealWorldEventGenerator.createNavigationTurn(),
                RealWorldEventGenerator.createMessage("Martin", "Llego en 5"),
                RealWorldEventGenerator.createWeatherUpdate("Cloudy", 15f)
            ),
            environmentalConditions = mapOf("TIME" to "NIGHT", "NOISE" to "HIGH")
        )

        val report = DeviceSimulationEngine.runScenario(scenario)
        LogPoseLogger.i("THAMIS_LAB: Reporte manual generado para '${report.scenarioName}'")
    }
}
