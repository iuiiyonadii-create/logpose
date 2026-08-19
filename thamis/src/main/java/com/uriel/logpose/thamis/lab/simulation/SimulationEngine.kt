package com.uriel.logpose.thamis.lab.simulation

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.lab.model.LabScenario

/**
 * Motor encargado de ejecutar las simulaciones de laboratorio.
 */
object SimulationEngine {

    fun executeScenario(scenario: LabScenario) {
        LogPoseLogger.i("THAMIS_LAB: Ejecutando escenario: ${scenario.name}")
        // Aquí se orquestarían las llamadas a ChaosController y EnvironmentSimulator
    }
}
