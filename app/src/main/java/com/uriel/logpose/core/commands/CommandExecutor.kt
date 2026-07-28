package com.uriel.logpose.core.commands

import android.util.Log
import com.uriel.logpose.core.music.MusicController

/**
 * Final stage of the command pipeline. Executes the validated action.
 */
class CommandExecutor(
    private val musicController: MusicController
) {
    fun execute(command: Command): Boolean {
        Log.d("CommandExecutor", "Executing: ${command.type}")
        return when (command.type) {
            CommandType.PLAY_MUSIC -> musicController.execute(com.uriel.logpose.domain.models.LogPoseCommand.PLAY)
            CommandType.PAUSE_MUSIC -> musicController.execute(com.uriel.logpose.domain.models.LogPoseCommand.PAUSE)
            CommandType.NEXT_TRACK -> musicController.execute(com.uriel.logpose.domain.models.LogPoseCommand.NEXT)
            CommandType.PREVIOUS_TRACK -> musicController.execute(com.uriel.logpose.domain.models.LogPoseCommand.PREVIOUS)
            CommandType.VOLUME_UP -> musicController.execute(com.uriel.logpose.domain.models.LogPoseCommand.VOLUME_UP)
            CommandType.VOLUME_DOWN -> musicController.execute(com.uriel.logpose.domain.models.LogPoseCommand.VOLUME_DOWN)
            else -> {
                Log.w("CommandExecutor", "Unsupported command execution: ${command.type}")
                false
            }
        }
    }
}
