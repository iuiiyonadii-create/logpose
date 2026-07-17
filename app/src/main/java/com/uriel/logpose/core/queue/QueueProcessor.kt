package com.uriel.logpose.core.queue

import com.uriel.logpose.core.execution.CommandExecutor

object QueueProcessor {

    fun process() {

        while (!CommandQueue.isEmpty()) {

            val queued =
                CommandQueue.poll()
                    ?: return

            CommandExecutor.execute(
                queued.command
            )
        }
    }
}