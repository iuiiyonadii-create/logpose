package com.uriel.logpose.thamis.communication.memory

import com.uriel.logpose.thamis.communication.model.CommunicationIntent

/**
 * Memoria inmediata de interacciones de comunicación.
 */
object CommunicationMemory {

    private val frequentEntities = mutableMapOf<String, Int>()
    private var lastContact: String? = null
    private var lastMessage: String? = null
    private var confirmationCount = 0

    fun recordInteraction(entity: String?, intent: CommunicationIntent) {
        entity?.let {
            val count = frequentEntities[it] ?: 0
            frequentEntities[it] = count + 1
            lastContact = it
        }
        if (intent == CommunicationIntent.SEND_MESSAGE) {
            // lastMessage = ...
        }
    }

    fun getEntityBonus(entity: String?): Float {
        if (entity == null) return 0f
        val count = frequentEntities[entity] ?: 0
        return if (count > 5) 0.20f else 0f
    }

    fun recordConfirmation() {
        confirmationCount++
    }

    fun clear() {
        frequentEntities.clear()
        lastContact = null
        lastMessage = null
        confirmationCount = 0
    }
}
