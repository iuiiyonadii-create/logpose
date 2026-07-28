package com.uriel.logpose.core.voice

import com.uriel.logpose.domain.models.LogPoseCommand

/**
 * Processes raw speech text into specific LogPose domain commands.
 */
class VoiceCommandProcessor {

    fun processText(input: String): LogPoseCommand {
        val cleanInput = input.lowercase().trim()
        
        return when {
            cleanInput.contains("reproducir") || cleanInput.contains("play") -> LogPoseCommand.PLAY
            cleanInput.contains("pausa") || cleanInput.contains("pause") -> LogPoseCommand.PAUSE
            cleanInput.contains("siguiente") || cleanInput.contains("next") -> LogPoseCommand.NEXT
            cleanInput.contains("anterior") || cleanInput.contains("previous") -> LogPoseCommand.PREVIOUS
            cleanInput.contains("subir volumen") || cleanInput.contains("volumen arriba") -> LogPoseCommand.VOLUME_UP
            cleanInput.contains("bajar volumen") || cleanInput.contains("volumen abajo") -> LogPoseCommand.VOLUME_DOWN
            else -> LogPoseCommand.UNKNOWN
        }
    }
}
