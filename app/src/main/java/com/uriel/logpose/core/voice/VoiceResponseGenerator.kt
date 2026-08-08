package com.uriel.logpose.core.voice

import com.thamis.lab.core.contracts.command.LogPoseCommand

/**
 * Generates natural language feedback based on system actions.
 */
object VoiceResponseGenerator {

    fun generate(command: LogPoseCommand): String {
        return when (command) {
            is LogPoseCommand.PlayMusic -> "Reproduciendo música."
            LogPoseCommand.PauseMusic -> "Música pausada."
            LogPoseCommand.NextTrack -> "Siguiente canción."
            LogPoseCommand.PreviousTrack -> "Canción anterior."
            LogPoseCommand.VolumeUp -> "Volumen aumentado."
            LogPoseCommand.VolumeDown -> "Volumen disminuido."
            LogPoseCommand.Unknown -> "No entendí ese comando."
            else -> ""
        }
    }
}
