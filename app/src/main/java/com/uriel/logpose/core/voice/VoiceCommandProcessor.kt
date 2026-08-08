package com.uriel.logpose.core.voice

import com.thamis.lab.core.contracts.command.LogPoseCommand

/**
 * Processes raw speech text into specific LogPose domain commands.
 */
class VoiceCommandProcessor {

    fun processText(input: String): LogPoseCommand {
        val cleanInput = input.lowercase().trim()
        
        return when {
            cleanInput.contains("reproducir") || cleanInput.contains("play") -> LogPoseCommand.PlayMusic("")
            cleanInput.contains("pausa") || cleanInput.contains("pause") -> LogPoseCommand.PauseMusic
            cleanInput.contains("siguiente") || cleanInput.contains("next") -> LogPoseCommand.NextTrack
            cleanInput.contains("anterior") || cleanInput.contains("previous") -> LogPoseCommand.PreviousTrack
            cleanInput.contains("subir volumen") || cleanInput.contains("volumen arriba") -> LogPoseCommand.VolumeUp
            cleanInput.contains("bajar volumen") || cleanInput.contains("volumen abajo") -> LogPoseCommand.VolumeDown
            else -> LogPoseCommand.Unknown
        }
    }
}
