package com.uriel.logpose.core.queue

import com.uriel.logpose.core.compat.core.Command

data class QueuedCommand(

    val command: Command,

    val timestamp: Long =
        System.currentTimeMillis()
)