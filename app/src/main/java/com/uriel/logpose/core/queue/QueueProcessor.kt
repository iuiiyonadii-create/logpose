package com.uriel.logpose.core.queue

import com.uriel.logpose.core.engine.CommandDispatcher

object QueueProcessor {

    fun process() {
        while (!CommandQueue.isEmpty()) {
            val queued = CommandQueue.poll() ?: return
            CommandDispatcher.execute(queued.command)
        }
    }
}
