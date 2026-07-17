package com.uriel.logpose.core.context

import com.uriel.logpose.core.compat.core.Command

data class CommandContext(

    val command: Command? = null,

    val originalText: String = "",

    val normalizedText: String = "",

    val success: Boolean = false,

    val timestamp: Long = System.currentTimeMillis()
)