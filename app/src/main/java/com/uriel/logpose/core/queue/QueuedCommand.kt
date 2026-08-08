package com.uriel.logpose.core.queue

import com.thamis.lab.core.contracts.command.LogPoseCommand

data class QueuedCommand(

    val command: LogPoseCommand,

    val timestamp: Long =
        System.currentTimeMillis()
)