package com.uriel.logpose.core.voice

import com.thamis.lab.core.contracts.command.LogPoseCommand

/**
 * Generates natural language feedback based on system actions.
 */
object VoiceResponseGenerator {

    fun generate(command: LogPoseCommand): String {
        return when (command) {
            is LogPoseCommand.Media.PlayMusic -> "Reproduciendo música."
            LogPoseCommand.Media.PauseMusic -> "Música pausada."
            LogPoseCommand.Media.NextTrack -> "Siguiente canción."
            LogPoseCommand.Media.PreviousTrack -> "Canción anterior."
            LogPoseCommand.System.VolumeUp -> "Volumen aumentado."
            LogPoseCommand.System.VolumeDown -> "Volumen disminuido."
            LogPoseCommand.Unknown -> "No entendí ese comando."
            else -> ""
        }
    }
}
