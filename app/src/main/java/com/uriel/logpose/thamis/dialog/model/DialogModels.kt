package com.uriel.logpose.thamis.dialog.model

import java.util.*

/**
 * Estados del motor conversacional.
 */
enum class DialogState {
    IDLE,
    LISTENING,
    WAITING_CONFIRMATION,
    WAITING_DESTINATION,
    WAITING_CONTACT,
    WAITING_RESPONSE,
    EXECUTING,
    FINISHED,
    CANCELLED,
    TIMEOUT
}

/**
 * Intenciones específicas de la conversación.
 */
enum class ConversationIntent {
    AFFIRMATIVE, // Sí
    NEGATIVE,    // No
    CANCEL,      // Cancelar
    WAIT,        // Esperar
    LATER,       // Después
    NEVER,       // Nunca
    ENTITY_SELECTION, // Ejemplo: "Pérez", "Trabajo"
    CONTINUE,    // Seguí
    UNKNOWN
}

/**
 * Representa una pregunta pendiente realizada por THAMIS.
 */
data class PendingQuestion(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val expectedDomain: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Representa una conversación activa.
 */
data class Conversation(
    val id: String = UUID.randomUUID().toString(),
    val domain: String,
    val turns: MutableList<ConversationTurn> = mutableListOf(),
    var state: DialogState = DialogState.IDLE,
    val startTime: Long = System.currentTimeMillis()
)

data class ConversationTurn(
    val speaker: Speaker,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class Speaker { USER, THAMIS }

/**
 * Decisión del motor de diálogo.
 */
data class DialogDecision(
    val nextState: DialogState,
    val response: String?,
    val actionToExecute: String? = null,
    val reason: String
)
