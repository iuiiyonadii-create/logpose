package com.uriel.logpose.thamis.autonomy

import com.thamis.lab.core.contracts.command.LogPoseCommand

/**
 * FASE 25.19 — THAMIS AUTONOMOUS ASSISTANCE FRAMEWORK
 * FASE 8: SAFETY BOUNDARIES
 */
object SafetyBoundaryManager {

    /**
     * Define si un comando es seguro para ser ejecutado autónomamente.
     */
    fun isSafeForAutonomousExecution(command: LogPoseCommand): Boolean {
        return when (command) {
            is LogPoseCommand.PlayMusic, LogPoseCommand.PauseMusic, LogPoseCommand.NextTrack -> true
            is LogPoseCommand.Call, is LogPoseCommand.SendMessage -> false // Requiere permiso explícito siempre
            LogPoseCommand.EndTrip -> false // Acción crítica
            else -> false
        }
    }
}
