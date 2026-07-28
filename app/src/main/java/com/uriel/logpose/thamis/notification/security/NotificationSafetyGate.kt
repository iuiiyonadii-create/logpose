package com.uriel.logpose.thamis.notification.security

import com.uriel.logpose.thamis.notification.model.NotificationDecision
import com.uriel.logpose.thamis.notification.model.NotificationPriority
import com.uriel.logpose.thamis.world.model.WorldSnapshot
import com.uriel.logpose.thamis.world.model.audio
import com.uriel.logpose.thamis.world.model.communication
import com.uriel.logpose.thamis.world.model.driving

/**
 * Valida si es seguro interrumpir al conductor con una notificación.
 */
object NotificationSafetyGate {

    fun determineAction(
        priority: NotificationPriority,
        worldSnapshot: WorldSnapshot
    ): NotificationDecision.Action {
        
        val speed = worldSnapshot.driving.speedKmh
        val isCallActive = worldSnapshot.communication.isCallActive
        
        // Regla 1: Bloqueo total > 120km/h excepto críticas
        if (speed > 120f && priority != NotificationPriority.CRITICAL) {
            return NotificationDecision.Action.IGNORE
        }

        // Regla 2: Durante llamada, no leer nada excepto críticas (SOS)
        if (isCallActive && priority != NotificationPriority.CRITICAL) {
            return NotificationDecision.Action.WAIT
        }

        // Regla 3: Si la música está muy alta, esperar a un momento más silencioso
        if (worldSnapshot.audio.volume > 90 && priority == NotificationPriority.LOW) {
            return NotificationDecision.Action.WAIT
        }

        return when (priority) {
            NotificationPriority.CRITICAL -> NotificationDecision.Action.READ_NOW
            NotificationPriority.HIGH -> NotificationDecision.Action.READ_NOW
            NotificationPriority.NORMAL -> NotificationDecision.Action.READ_NOW
            NotificationPriority.LOW -> NotificationDecision.Action.WAIT
            NotificationPriority.SILENT -> NotificationDecision.Action.IGNORE
        }
    }
}
