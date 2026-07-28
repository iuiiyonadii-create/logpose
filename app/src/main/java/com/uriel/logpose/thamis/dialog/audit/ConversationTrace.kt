package com.uriel.logpose.thamis.dialog.audit

import com.uriel.logpose.thamis.dialog.model.DialogState

/**
 * Registro auditable de un turno de conversación.
 */
data class ConversationTrace(
    val previousState: DialogState,
    val newState: DialogState,
    val question: String?,
    val response: String?,
    val durationMs: Long,
    val reason: String,
    val timestamp: Long = System.currentTimeMillis()
)

object ConversationAudit {
    private val log = mutableListOf<ConversationTrace>()

    fun record(trace: ConversationTrace) {
        log.add(trace)
        if (log.size > 100) log.removeAt(0)
    }

    fun getLogs(): List<ConversationTrace> = log.toList()
}
