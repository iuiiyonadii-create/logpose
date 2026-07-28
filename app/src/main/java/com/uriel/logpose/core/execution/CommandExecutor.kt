package com.uriel.logpose.core.execution

import com.uriel.logpose.core.compat.core.Command
import com.uriel.logpose.core.engine.CommandDispatcher

/**
 * Ejecutor de comandos centralizado.
 */
object CommandExecutor {

    fun execute(command: Command) {
        CommandDispatcher.execute(command)
    }
}
