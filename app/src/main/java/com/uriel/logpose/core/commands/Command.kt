package com.uriel.logpose.core.commands

import java.util.UUID

/**
 * Model representing a specific user command instance.
 */
data class Command(
    val id: String = UUID.randomUUID().toString(),
    val type: CommandType,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val source: CommandSource = CommandSource.VOICE
)

enum class CommandSource {
    VOICE,
    UI,
    PHYSICAL_BUTTON,
    SYSTEM
}
