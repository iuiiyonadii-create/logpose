package com.uriel.logpose.thamis.journey.security

import com.uriel.logpose.thamis.journey.model.JourneyState
import com.uriel.logpose.thamis.journey.model.JourneyTransition

/**
 * Gate de seguridad encargado de validar transiciones imposibles o peligrosas.
 */
object JourneySafetyGate {
    
    /**
     * Valida si una transición es permitida bajo las condiciones actuales.
     */
    fun isTransitionSafe(transition: JourneyTransition, gpsAvailable: Boolean, speed: Float): Boolean {
        // Regla: No puede iniciar viaje (MOVING) sin GPS válido si la velocidad es baja
        if (transition.to == JourneyState.MOVING && !gpsAvailable && speed < 5f) {
            return false
        }
        
        // Regla: No puede finalizar viaje si sigue desplazándose significativamente
        if (transition.to == JourneyState.FINISHED && speed > 10f) {
            return false
        }
        
        // Regla: Prohibido saltar de OFF a MOVING directo sin estados intermedios
        if (transition.from == JourneyState.OFF && transition.to == JourneyState.MOVING) {
            return false
        }
        
        return true
    }
}
