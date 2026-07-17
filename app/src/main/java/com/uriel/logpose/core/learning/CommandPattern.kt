package com.uriel.logpose.core.learning

import com.uriel.logpose.core.compat.core.Command

data class CommandPattern(
    val command: Command,
    val executions: Int = 0,
    val lastUsed: Long = System.currentTimeMillis()
)