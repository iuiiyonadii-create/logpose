package com.uriel.logpose.thamis.autonomy

import com.uriel.logpose.core.compat.core.Command

/**
 * FASE 25.19 — THAMIS AUTONOMOUS ASSISTANCE FRAMEWORK
 * FASE 8: SAFETY BOUNDARIES
 */
object SafetyBoundaryManager {

    /**
     * Define si un comando es seguro para ser ejecutado autónomamente.
     */
    fun isSafeForAutonomousExecution(command: Command): Boolean {
        return when (command) {
            is Command.PlayMusic, is Command.PauseMusic, is Command.NextTrack -> true
            is Command.Call, is Command.SendMessage -> false // Requiere permiso explícito siempre
            is Command.EndTrip -> false // Acción crítica
            else -> false
        }
    }
}
