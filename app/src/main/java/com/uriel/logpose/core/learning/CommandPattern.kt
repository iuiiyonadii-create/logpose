package com.uriel.logpose.core.learning

import com.thamis.lab.core.contracts.command.LogPoseCommand

data class CommandPattern(
    val command: LogPoseCommand,
    val executions: Int = 0,
    val lastUsed: Long = System.currentTimeMillis()
)