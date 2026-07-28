package com.uriel.logpose.thamis.dialog.memory

/**
 * Memoria a corto plazo de la conversación.
 */
object ConversationMemory {
    var lastQuestion: String? = null
    var lastResponse: String? = null
    var lastContact: String? = null
    var lastDestination: String? = null
    var lastSongInfo: String? = null
    var lastInteractionTimestamp: Long = 0

    fun recordInteraction() {
        lastInteractionTimestamp = System.currentTimeMillis()
    }

    fun getTimeSinceLastInteraction(): Long {
        if (lastInteractionTimestamp == 0L) return Long.MAX_VALUE
        return System.currentTimeMillis() - lastInteractionTimestamp
    }

    fun clear() {
        lastQuestion = null
        lastResponse = null
        lastContact = null
        lastDestination = null
        lastSongInfo = null
        lastInteractionTimestamp = 0
    }
}
