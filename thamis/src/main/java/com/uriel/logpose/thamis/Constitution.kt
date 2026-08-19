package com.uriel.logpose.thamis

import com.uriel.logpose.thamis.cognitive.model.WorldState
import com.thamis.lab.core.contracts.intent.Intent

/**
 * La Constitución de THAMIS define las leyes fundamentales de comportamiento.
 * Hardened v1.5: Validación real de acciones basada en las Leyes de la Robótica adaptadas al Motociclismo.
 */
object Constitution {

    fun isActionAllowed(intent: Intent, worldState: WorldState): Boolean {
        // LEY 0: Seguridad física. No permitir distracciones complejas a alta velocidad.
        if (worldState.driving.speedKmh > 130) {
            // Solo permitir alertas críticas o modo emergencia
            return intent == Intent.EMERGENCY
        }

        // LEY 1: Prioridad de Atención. No interrumpir llamadas con navegación secundaria.
        if (worldState.system.activeCall && intent == Intent.NAVIGATE) {
            return false
        }

        // LEY 2: No molestar en reposo.
        if (!worldState.driving.isMoving && !worldState.system.isHeadsetConnected) {
             // Si está detenido y sin casco, ser extremadamente discreto.
             // (Lógica futura de feedback reducido)
        }

        return true
    }

    /**
     * Ley 0: La seguridad del conductor es la prioridad absoluta.
     */
    const val LAW_SAFETY_FIRST = "safety_first"

    /**
     * Ley 1: THAMIS debe ser invisible pero estar siempre disponible.
     */
    const val LAW_MINIMAL_INTERACTION = "minimal_interaction"

    /**
     * Ley 2: Privacidad por defecto.
     */
    const val LAW_PRIVACY_BY_DEFAULT = "privacy_default"
}
