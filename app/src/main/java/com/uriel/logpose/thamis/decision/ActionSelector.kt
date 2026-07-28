package com.uriel.logpose.thamis.decision

import com.uriel.logpose.core.Command
import com.uriel.logpose.core.Action

/**
 * FASE 26.12 — LOGPOSE THAMIS ADVANCED DECISION ENGINE
 * FASE 5: ACTION SELECTOR
 */
object ActionSelector {

    fun selectAction(command: Command): Action {
        return when (command) {
            Command.PLAY_MUSIC, Command.PAUSE_MUSIC, 
            Command.NEXT_TRACK, Command.PREVIOUS_TRACK -> Action.MediaAction(command)
            
            Command.CALL_CONTACT -> Action.CallAction("Pendiente")
            
            Command.STOP_LOGPOSE -> Action.StopService
            
            else -> Action.VoiceResponse("Comando no implementado aún.")
        }
    }
}
