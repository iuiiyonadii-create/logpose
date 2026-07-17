package com.uriel.logpose.core.execution

import com.uriel.logpose.core.compat.core.Command
import com.uriel.logpose.core.engine.CommandDispatcher

object CommandExecutor {

    fun execute(
        command: Command
    ): ExecutionResult {

        return try {

            CommandDispatcher.execute(command)

            ExecutionResult(
                status = ExecutionStatus.SUCCESS
            )

        } catch (e: Exception) {

            ExecutionResult(

                status = ExecutionStatus.FAILED,

                message = e.message
            )
        }
    }
}