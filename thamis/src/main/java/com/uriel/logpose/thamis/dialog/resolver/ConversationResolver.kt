package com.uriel.logpose.thamis.dialog.resolver

import com.uriel.logpose.thamis.dialog.confirmation.ConfirmationEngine
import com.uriel.logpose.thamis.dialog.context.DialogContext
import com.uriel.logpose.thamis.dialog.model.*

/**
 * Resuelve la lógica de unión entre respuestas parciales y el contexto.
 */
object ConversationResolver {

    fun resolve(input: String): DialogDecision {
        val pendingQuestion = DialogContext.getPendingQuestion()
        val intent = ConfirmationEngine.resolveIntent(input)

        if (pendingQuestion == null) {
            return DialogDecision(DialogState.IDLE, null, reason = "No hay pregunta pendiente")
        }

        return when (intent) {
            ConversationIntent.AFFIRMATIVE -> DialogDecision(DialogState.EXECUTING, "Haciendo.", pendingQuestion.expectedDomain, "Usuario confirmó")
            ConversationIntent.NEGATIVE -> DialogDecision(DialogState.CANCELLED, "Cancelado.", null, "Usuario denegó")
            ConversationIntent.CANCEL -> DialogDecision(DialogState.CANCELLED, "Ok, paro.", null, "Usuario canceló")
            else -> {
                // Si no es un SI/NO, podría ser una selección de entidad (ej: "Pérez")
                DialogDecision(DialogState.FINISHED, "Entendido: $input", pendingQuestion.expectedDomain, "Selección de entidad")
            }
        }
    }
}
