package com.uriel.logpose.core.voice

import com.thamis.lab.core.contracts.command.LogPoseCommand

/**
 * Processes raw speech text into specific LogPose domain commands.
 */
class VoiceCommandProcessor {

    fun processText(input: String): LogPoseCommand {
        val cleanInput = input.lowercase().trim()
        
        return when {
            cleanInput.contains("reproducir") || cleanInput.contains("play") -> LogPoseCommand.Media.PlayMusic("")
            cleanInput.contains("pausa") || cleanInput.contains("pause") -> LogPoseCommand.Media.PauseMusic
            cleanInput.contains("siguiente") || cleanInput.contains("next") -> LogPoseCommand.Media.NextTrack
            cleanInput.contains("anterior") || cleanInput.contains("previous") -> LogPoseCommand.Media.PreviousTrack
            cleanInput.contains("subir volumen") || cleanInput.contains("volumen arriba") -> LogPoseCommand.System.VolumeUp
            cleanInput.contains("bajar volumen") || cleanInput.contains("volumen abajo") -> LogPoseCommand.System.VolumeDown
            else -> LogPoseCommand.Unknown
        }
    }
}
