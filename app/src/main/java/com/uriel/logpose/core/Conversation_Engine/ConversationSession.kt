package com.uriel.logpose.core.conversation

data class ConversationSession(

    val id: Long = System.currentTimeMillis(),

    var state: ConversationState = ConversationState.IDLE,

    var lastQuestion: String = "",

    var lastAnswer: String = ""
)