package com.uriel.logpose.thamis.navigation

import com.uriel.logpose.thamis.cognitive.model.WorldState

/**
 * Prioriza acciones críticas del sistema sobre la navegación.
 * Si el usuario está en una situación de riesgo o comunicación, la navegación debe esperar.
 */
object NavigationPriorityGuard {

    fun shouldWait(worldState: WorldState): Boolean {
        // Prioridad superior: Llamadas activas
        if (worldState.system.activeCall) return true
        
        // Prioridad superior: Alertas críticas (Simulado: batería baja extrema, etc)
        if (worldState.system.batteryPct < 5) return true

        return false
    }
}
