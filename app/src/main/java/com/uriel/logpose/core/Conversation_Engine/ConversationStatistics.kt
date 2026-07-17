package com.uriel.logpose.core.conversation

object ConversationStatistics {

    private var conversations = 0

    fun registerConversation() {

        conversations++
    }

    fun totalConversations(): Int =
        conversations
}