package com.uriel.logpose.core.commands

import android.util.Log
import com.thamis.lab.core.contracts.command.LogPoseCommand
import com.uriel.logpose.core.music.MusicController

/**
 * Final stage of the command pipeline. Executes the validated action.
 */
class CommandExecutor(
    private val musicController: MusicController
) {
    fun execute(command: LogPoseCommand): Boolean {
        Log.d("CommandExecutor", "Executing: ${command::class.simpleName}")
        return musicController.execute(command)
    }
}
