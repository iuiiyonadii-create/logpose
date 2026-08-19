package com.uriel.logpose.thamis.desktop_lab.app

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.desktop_lab.model.DesktopLabState
import com.uriel.logpose.thamis.lab.simulation.DeviceSimulationEngine
import com.uriel.logpose.thamis.lab.model.SimulationScenario

/**
 * Motor central de la aplicación de escritorio para el control del laboratorio.
 */
object DesktopLabEngine {
    private var currentState = DesktopLabState()
    private val consoleHistory = mutableListOf<String>()

    fun startSimulation(scenario: SimulationScenario) {
        currentState = currentState.copy(isRunning = true, activeScenarioId = scenario.id)
        LogPoseLogger.i("THAMIS_DESKTOP: Iniciando simulación remota...")
        
        val report = DeviceSimulationEngine.runScenario(scenario)
        
        currentState = currentState.copy(
            isRunning = false, 
            totalTestsRun = currentState.totalTestsRun + 1,
            totalErrorsDetected = currentState.totalErrorsDetected + report.errorsDetected.size
        )
    }

    fun getStatus(): DesktopLabState = currentState

    fun logToConsole(msg: String) {
        consoleHistory.add(msg)
        if (consoleHistory.size > 1000) consoleHistory.removeAt(0)
    }
}
