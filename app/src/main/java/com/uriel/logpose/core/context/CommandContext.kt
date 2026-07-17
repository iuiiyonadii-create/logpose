package com.uriel.logpose.core.context

import com.uriel.logpose.core.compat.core.Command

data class CommandContext(
    val command: Command,
    val timestamp: Long = System.currentTimeMillis(),
    val success: Boolean = true
)