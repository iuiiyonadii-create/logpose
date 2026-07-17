package com.uriel.logpose.core.conversation

object ConversationRepository {

    private val history =
        mutableListOf<ConversationContext>()

    fun add(
        context: ConversationContext
    ) {

        history.add(context)
    }

    fun last(): ConversationContext? =
        history.lastOrNull()

    fun all(): List<ConversationContext> =
        history.toList()

    fun clear() {

        history.clear()
    }
}