package com.uriel.logpose.thamis.lab.scenario

import com.uriel.logpose.thamis.lab.events.RealWorldEventGenerator
import com.uriel.logpose.thamis.lab.model.SimulationScenario

/**
 * Biblioteca de escenarios estándar para validación rápida.
 */
object ScenarioLibrary {

    val AUTOPISTA_ALTA_VELOCIDAD = SimulationScenario(
        name = "Autopista Alta Velocidad",
        description = "Simulación en ruta con viento y navegación prioritaria.",
        eventSequence = listOf(
            RealWorldEventGenerator.createNavigationTurn("Continúe 10km por la ruta", 10000),
            RealWorldEventGenerator.createBluetoothDisconnect()
        ),
        environmentalConditions = mapOf("SPEED" to "120KMH", "WIND" to "HIGH")
    )

    val ENTREGA_DELIVERY = SimulationScenario(
        name = "Entrega Delivery",
        description = "Alta carga de notificaciones y mensajes cortos.",
        eventSequence = listOf(
            RealWorldEventGenerator.createMessage("App", "Nuevo pedido asignado", true),
            RealWorldEventGenerator.createMessage("App", "Retirar en local 4"),
            RealWorldEventGenerator.createNavigationTurn()
        ),
        environmentalConditions = mapOf("TRAFFIC" to "HEAVY")
    )
}
