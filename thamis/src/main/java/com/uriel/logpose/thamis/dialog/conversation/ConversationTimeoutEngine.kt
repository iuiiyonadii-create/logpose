package com.uriel.logpose.thamis.dialog.conversation

import com.uriel.logpose.thamis.dialog.context.DialogContext

/**
 * Gestiona la expiración de las conversaciones.
 */
object ConversationTimeoutEngine {
    private const val TIMEOUT_MS = 20_000L

    fun checkTimeout() {
        val question = DialogContext.getPendingQuestion() ?: return
        val now = System.currentTimeMillis()

        if (now - question.timestamp > TIMEOUT_MS) {
            DialogContext.clear()
            // Log timeout
        }
    }
}
