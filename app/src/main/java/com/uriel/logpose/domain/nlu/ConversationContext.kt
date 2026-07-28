package com.uriel.logpose.domain.nlu

/**
 * State of current dialogue for reference resolution.
 */
data class ConversationContext(
    val lastIntent: UserIntent? = null,
    val activeEntity: Entity? = null,
    val turnsCount: Int = 0
)
