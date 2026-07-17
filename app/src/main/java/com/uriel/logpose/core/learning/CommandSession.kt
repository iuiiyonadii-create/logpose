package com.uriel.logpose.core.learning

data class CommandSession(

    val command: String,

    val timestamp: Long,

    val success: Boolean
)