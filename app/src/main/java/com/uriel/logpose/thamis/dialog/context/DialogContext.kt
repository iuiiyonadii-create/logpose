package com.uriel.logpose.thamis.dialog.context

import com.uriel.logpose.thamis.dialog.model.*

/**
 * Mantiene el contexto vivo de la conversación actual.
 */
object DialogContext {
    private var activeConversation: Conversation? = null
    private var pendingQuestion: PendingQuestion? = null

    fun setConversation(conversation: Conversation) {
        activeConversation = conversation
    }

    fun getConversation(): Conversation? = activeConversation

    fun setPendingQuestion(question: PendingQuestion) {
        pendingQuestion = question
    }

    fun getPendingQuestion(): PendingQuestion? = pendingQuestion

    fun clear() {
        activeConversation = null
        pendingQuestion = null
    }

    fun isUserSpeaking(): Boolean {
        return activeConversation?.state == DialogState.LISTENING
    }
}
