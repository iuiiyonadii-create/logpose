package com.uriel.logpose.thamis.lab.scenario

import com.uriel.logpose.thamis.lab.model.LabScenario

/**
 * Administra y organiza los escenarios de prueba en el laboratorio.
 */
object ScenarioManager {
    private val scenarios = mutableListOf<LabScenario>()

    fun registerScenario(scenario: LabScenario) {
        scenarios.add(scenario)
    }

    fun getScenario(id: String): LabScenario? = scenarios.find { it.id == id }

    fun getAllScenarios(): List<LabScenario> = scenarios.toList()
}
