package com.uriel.logpose.thamis.intelligence.llm.tool

import com.uriel.logpose.thamis.world.engine.WorldModelEngine

/**
 * AndroidToolbox v1.1: El set de herramientas MCP para Llama 3.2.
 * Permite que la IA consulte el estado real del hardware y el mundo.
 */
object AndroidToolbox {

    /**
     * Devuelve un resumen del estado del sistema para inyectar en el prompt.
     */
    fun getSystemContext(): String {
        val snapshot = WorldModelEngine.getCurrentSnapshot()
        val battery = snapshot.systems.device.batteryPct
        val speed = snapshot.vehicle.speedKmh
        
        return """
            [ESTADO DEL SISTEMA]
            Batería: $battery%
            Velocidad: $speed km/h
            Navegando: ${if (snapshot.systems.navigation.isNavigating) "Sí" else "No"}
            Música sonando: ${if (snapshot.systems.audio.isPlaying) "Sí" else "No"}
        """.trimIndent()
    }

    /**
     * Herramientas ejecutables que la IA puede "llamar".
     */
    fun executeTool(toolName: String, params: Map<String, String>): String {
        return when (toolName) {
            "GET_BATTERY" -> "Nivel de batería: ${WorldModelEngine.getCurrentSnapshot().systems.device.batteryPct}%"
            "GET_SPEED" -> "Velocidad actual: ${WorldModelEngine.getCurrentSnapshot().vehicle.speedKmh} km/h"
            else -> "Herramienta no encontrada."
        }
    }
}
