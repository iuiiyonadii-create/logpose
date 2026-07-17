package com.uriel.logpose.core.conversation

object ConversationMemory {

    private var current: ConversationSession? = null

    fun start() {

        current = ConversationSession()
    }

    fun session(): ConversationSession? =
        current

    fun finish() {

        current = null
    }

    fun active(): Boolean =
        current != null
}