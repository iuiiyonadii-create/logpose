package com.uriel.logpose.core.execution

import com.thamis.lab.core.contracts.command.LogPoseCommand
import com.uriel.logpose.core.engine.CommandDispatcher

/**
 * Ejecutor de comandos centralizado.
 */
object CommandExecutor {

    fun execute(command: LogPoseCommand) {
        CommandDispatcher.execute(command)
    }
}
