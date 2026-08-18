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
            is LogPoseCommand.Media.PlayMusic, 
            LogPoseCommand.Media.PauseMusic, 
            LogPoseCommand.Media.NextTrack -> true
            is LogPoseCommand.Communication.Call, 
            is LogPoseCommand.Communication.SendMessage -> false // Requiere permiso explícito siempre
            LogPoseCommand.EndTrip -> false // Acción crítica
            else -> false
        }
    }
}
