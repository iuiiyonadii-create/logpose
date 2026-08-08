package com.uriel.logpose.core.context

import com.thamis.lab.core.contracts.command.LogPoseCommand

data class CommandContext(

    val command: LogPoseCommand? = null,

    val originalText: String = "",

    val normalizedText: String = "",

    val success: Boolean = false,

    val timestamp: Long = System.currentTimeMillis()
)