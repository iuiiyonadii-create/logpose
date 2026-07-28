package com.uriel.logpose.thamis.dialog.confirmation

import com.uriel.logpose.thamis.dialog.model.ConversationIntent
import java.util.*

/**
 * Gestiona respuestas afirmativas, negativas y de control.
 */
object ConfirmationEngine {

    fun resolveIntent(input: String): ConversationIntent {
        val query = input.lowercase(Locale.getDefault()).trim()

        return when {
            query.contains("si") || query.contains("sí") || query.contains("dale") || query.contains("hacelo") -> ConversationIntent.AFFIRMATIVE
            query.contains("no") || query.contains("negativo") || query.contains("ni a palos") -> ConversationIntent.NEGATIVE
            query.contains("cancela") || query.contains("cancelá") || query.contains("pará") || query.contains("basta") -> ConversationIntent.CANCEL
            query.contains("espera") || query.contains("aguanta") || query.contains("aguantá") -> ConversationIntent.WAIT
            query.contains("después") || query.contains("luego") -> ConversationIntent.LATER
            query.contains("nunca") || query.contains("jamás") -> ConversationIntent.NEVER
            query.contains("seguí") || query.contains("continua") || query.contains("continuá") -> ConversationIntent.CONTINUE
            else -> ConversationIntent.UNKNOWN
        }
    }
}
