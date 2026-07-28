package com.uriel.logpose.core.voice

import com.uriel.logpose.domain.models.LogPoseCommand

/**
 * Generates natural language feedback based on system actions.
 */
object VoiceResponseGenerator {

    fun generate(command: LogPoseCommand): String {
        return when (command) {
            LogPoseCommand.PLAY -> "Reproduciendo música."
            LogPoseCommand.PAUSE -> "Música pausada."
            LogPoseCommand.NEXT -> "Siguiente canción."
            LogPoseCommand.PREVIOUS -> "Canción anterior."
            LogPoseCommand.VOLUME_UP -> "Volumen aumentado."
            LogPoseCommand.VOLUME_DOWN -> "Volumen disminuido."
            LogPoseCommand.UNKNOWN -> "No entendí ese comando."
            else -> ""
        }
    }
}
