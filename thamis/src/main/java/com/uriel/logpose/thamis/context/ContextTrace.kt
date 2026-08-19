package com.uriel.logpose.thamis.context

/**
 * Registro de cambios en el estado cognitivo.
 */
data class ContextTrace(
    val previousState: ConversationState,
    val newState: ConversationState,
    val reason: String,
    val domain: FocusDomain,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Valida la integridad del contexto antes de recuperarlo o usarlo para decisiones críticas.
 */
object ContextValidator {
    fun isValid(context: SessionContext): Boolean {
        // Un contexto es inválido si su inicio es en el futuro o si tiene inconsistencias graves
        if (context.startTime > System.currentTimeMillis()) return false
        
        // Si el estado es WAITING_CONFIRMATION pero no hay acción pendiente, es inconsistente
        if (context.conversationState == ConversationState.WAITING_CONFIRMATION && context.pendingAction == null) {
            return false
        }

        return true
    }
}
