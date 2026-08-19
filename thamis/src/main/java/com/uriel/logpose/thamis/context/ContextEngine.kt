package com.uriel.logpose.thamis.context

/**
 * ContextEngine v3.4: El motor de memoria a corto plazo de THAMIS.
 * Mantiene la coherencia de la sesión y el estado vivo del usuario.
 */
object ContextEngine {

    private var activeContext = SessionContext()
    private val traces = mutableListOf<ContextTrace>()

    /**
     * Actualiza el estado de la conversación.
     */
    fun updateState(newState: ConversationState, reason: String, domain: FocusDomain = FocusDomain.NONE) {
        val previous = activeContext.conversationState
        activeContext.conversationState = newState
        
        if (domain != FocusDomain.NONE) {
            FocusManager.requestFocus(domain)
            activeContext.currentFocus = domain
        }

        recordTrace(previous, newState, reason, activeContext.currentFocus)
    }

    /**
     * Registra un nuevo snapshot del mundo.
     */
    fun takeSnapshot(snapshot: ContextSnapshot) {
        activeContext.addSnapshot(snapshot)
        ContextMetrics.recordSnapshot()
    }

    /**
     * Establece una acción esperando respuesta.
     */
    fun setPendingAction(action: PendingAction) {
        activeContext.pendingAction = action
        updateState(ConversationState.WAITING_CONFIRMATION, "Esperando respuesta del usuario", action.domain)
    }

    /**
     * Obtiene la acción pendiente si no ha expirado.
     */
    fun getPendingAction(): PendingAction? {
        activeContext.clearExpiredActions()
        return activeContext.pendingAction
    }

    fun clearPendingAction() {
        activeContext.pendingAction = null
    }

    /**
     * Recuperación de contexto tras reinicio del sistema.
     */
    fun recover(savedContext: SessionContext) {
        if (ContextValidator.isValid(savedContext)) {
            activeContext = savedContext
            ContextMetrics.recordRecovery()
        }
    }

    fun getActiveContext(): SessionContext = activeContext

    private fun recordTrace(prev: ConversationState, next: ConversationState, reason: String, domain: FocusDomain) {
        val trace = ContextTrace(prev, next, reason, domain)
        traces.add(trace)
        if (traces.size > 100) traces.removeAt(0)
    }

    fun getTraces(): List<ContextTrace> = traces.toList()
}
