package com.uriel.logpose.core

/**
 * FASE 26.2 — LOGPOSE MVP CORE
 * FASE 7: ACTION MANAGER
 *
 * Representa una acción física o lógica que el sistema debe ejecutar.
 */
sealed class Action {
    data class MediaAction(val command: Command) : Action()
    data class CallAction(val contactName: String) : Action()
    data class NotificationAction(val readAll: Boolean) : Action()
    data class VoiceResponse(val message: String) : Action()
    object StopService : Action()
}
