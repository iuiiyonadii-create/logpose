package com.uriel.logpose.thamis_ai.voice

import com.uriel.logpose.domain.nlu.ConversationContext
import com.uriel.logpose.domain.nlu.NluResult

/**
 * Manages short multi-turn dialogue trees.
 */
class DialogueManager {

    private var currentContext = ConversationContext()

    fun update(result: NluResult): String? {
        // Logic for state transitions (e.g. waiting for confirmation)
        return null
    }

    fun getContext() = currentContext
}
