package com.uriel.logpose.thamis.voiceexperience.interrupt

import com.uriel.logpose.thamis.voiceexperience.model.VoicePriority
import com.uriel.logpose.thamis.voiceexperience.model.DrivingContext

/**
 * Controla si un mensaje nuevo puede interrumpir la situación actual.
 */
object InterruptionManager {

    fun shouldInterrupt(newPriority: VoicePriority, context: DrivingContext): Boolean {
        // Regla de Oro: Emergencia siempre interrumpe.
        if (newPriority == VoicePriority.EMERGENCY) return true

        // Durante llamada activa, solo interrumpir con Seguridad o Emergencia.
        if (context.isCallActive && newPriority.level < VoicePriority.SAFETY.level) return false

        // Si la carga cognitiva es alta, bloquear multimedia e información.
        if (context.cognitiveLoad > 0.8f && newPriority.level < VoicePriority.NAVIGATION.level) return false

        return true
    }
}
