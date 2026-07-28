package com.uriel.logpose.thamis.orchestrator

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.Command
import com.uriel.logpose.core.compat.core.Command as CompatCommand
import com.uriel.logpose.core.engine.CommandDispatcher
import com.uriel.logpose.thamis.decision.DecisionEngine
import com.uriel.logpose.thamis.context.ContextEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * FASE 26.13 — LOGPOSE THAMIS SYSTEM ORCHESTRATOR
 * FASE 1: SYSTEM ORCHESTRATOR CORE
 */
object SystemOrchestrator {

    private val _systemHealth = MutableStateFlow(100)
    val systemHealth = _systemHealth.asStateFlow()

    fun initialize() {
        LogPoseLogger.i("SystemOrchestrator: Inicializando núcleo THAMIS...")
        // Aquí se inicializarían los módulos en orden
    }

    /**
     * Punto de entrada para eventos de alto nivel en el sistema.
     */
    fun onEvent(event: String) {
        LogPoseLogger.d("SystemOrchestrator: Evento recibido: $event")
        // En un caso real, transformaríamos el evento en un Command
    }

    fun dispatchCommand(command: Command) {
        val action = DecisionEngine.evaluate(command)
        LogPoseLogger.i("SystemOrchestrator: Acción decidida: $action")
        // Ejecutar la acción a través del ActionManager
    }

    /**
     * Despacha comandos de compatibilidad (Sprint 26+) al despachador central.
     */
    fun dispatchCompatCommand(command: CompatCommand) {
        LogPoseLogger.i("SystemOrchestrator: Despachando comando compat: ${command::class.simpleName}")
        CommandDispatcher.execute(command)
    }

    fun updateHealth(newHealth: Int) {
        _systemHealth.value = newHealth
        LogPoseLogger.i("SystemOrchestrator: Salud del sistema actualizada: $newHealth%")
    }
}
