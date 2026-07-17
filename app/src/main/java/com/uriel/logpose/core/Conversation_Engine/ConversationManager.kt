package com.uriel.logpose.core.conversation

object ConversationManager {

    fun start() {

        ConversationMemory.start()

        ConversationStatistics.registerConversation()
    }

    fun finish() {

        ConversationMemory.finish()
    }

    fun active(): Boolean =
        ConversationMemory.active()

    fun session(): ConversationSession? =
        ConversationMemory.session()
}