package com.uriel.logpose.core.parser.context

data class CommandContext(
    val originalText: String,
    val normalizedText: String,
    val timestamp: Long = System.currentTimeMillis()
)