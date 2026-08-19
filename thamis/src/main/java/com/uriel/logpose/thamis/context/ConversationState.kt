package com.uriel.logpose.thamis.context

/**
 * Estados posibles de la interacción verbal con THAMIS.
 */
enum class ConversationState {
    IDLE,                  // En espera
    LISTENING,             // Escuchando audio
    PROCESSING,            // Analizando intención
    WAITING_CONFIRMATION,  // Esperando un "Sí/No" o elección
    EXECUTING,             // Ejecutando acción
    INTERRUPTED,           // Pausado por evento de mayor prioridad
    CANCELLED,             // Operación abortada
    FINISHED               // Ciclo completado con éxito
}

/**
 * Dominios que pueden tener el foco del sistema.
 */
enum class FocusDomain {
    MULTIMEDIA,
    NAVIGATION,
    CALL,
    MESSAGE,
    SETTINGS,
    EMERGENCY,
    SYSTEM,
    NONE
}
