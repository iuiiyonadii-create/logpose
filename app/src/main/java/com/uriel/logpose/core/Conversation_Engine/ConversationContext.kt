package com.uriel.logpose.core.conversation

data class ConversationContext(

    val lastCommand: String = "",

    val lastEntity: String = "",

    val lastResponse: String = "",

    val createdAt: Long =
        System.currentTimeMillis()
)