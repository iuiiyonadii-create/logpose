package com.uriel.logpose.thamis.experience

/**
 * FASE 25.17 — THAMIS PERSONAL AI ASSISTANT EXPERIENCE
 * FASE 12: PERSONAL ASSISTANT RULES
 *
 * Reglas de oro que rigen el comportamiento de THAMIS como asistente personal.
 */
object AssistantRules {
    
    /**
     * Regla 1: Nunca interrumpir sin motivo de seguridad o urgencia.
     */
    const val NEVER_INTERRUPT_WITHOUT_REASON = true
    
    /**
     * Regla 2: Nunca inventar (alucinar) información. 
     * Si no se sabe, se admite la falta de datos.
     */
    const val NEVER_INVENT_INFORMATION = true
    
    /**
     * Regla 3: Siempre explicar acciones importantes o cambios de estado.
     */
    const val ALWAYS_EXPLAIN_IMPORTANT_ACTIONS = true

    /**
     * Valida si un mensaje cumple con las reglas de asistencia.
     */
    fun validateMessage(message: String): Boolean {
        // En el futuro, esto podría usar NLU para detectar alucinaciones o interrupciones indebidas.
        return message.isNotEmpty()
    }
}
