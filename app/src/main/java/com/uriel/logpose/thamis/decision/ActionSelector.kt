package com.uriel.logpose.thamis.decision

import com.thamis.lab.core.contracts.command.LogPoseCommand
import com.uriel.logpose.core.Action

/**
 * FASE 26.12 — LOGPOSE THAMIS ADVANCED DECISION ENGINE
 * FASE 5: ACTION SELECTOR
 */
object ActionSelector {

    fun selectAction(command: LogPoseCommand): Action {
        return when (command) {
            is LogPoseCommand.PlayMusic, LogPoseCommand.PauseMusic, 
            LogPoseCommand.NextTrack, LogPoseCommand.PreviousTrack -> Action.MediaAction(command)
            
            is LogPoseCommand.Call -> Action.CallAction(command.contact)
            
            LogPoseCommand.StopListening -> Action.StopService
            
            else -> Action.VoiceResponse("Comando no implementado aún.")
        }
    }
}
