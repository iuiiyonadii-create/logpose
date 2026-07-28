package com.uriel.logpose.thamis.voiceexperience.engine

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.voiceexperience.model.*
import com.uriel.logpose.thamis.voiceexperience.priority.VoicePriorityEngine
import com.uriel.logpose.thamis.voiceexperience.interrupt.InterruptionManager
import com.uriel.logpose.thamis.voiceexperience.response.ResponseStyleEngine
import com.uriel.logpose.thamis.voiceexperience.silence.SilenceManager
import com.uriel.logpose.thamis.voiceexperience.timing.VoiceTimingManager

/**
 * Motor principal de experiencia vocal de conducción THAMIS v1.0.
 */
object DrivingVoiceExperienceEngine {

    fun evaluate(rawMessage: String, category: String, context: DrivingContext): VoiceDecision {
        val priority = VoicePriorityEngine.resolvePriority(category)
        
        // 1. Validar Interrupción
        val shouldProceed = InterruptionManager.shouldInterrupt(priority, context)
        if (!shouldProceed) {
            return VoiceDecision(rawMessage, priority, "REJECTED_BY_INTERRUPTION", 0, 0f, ResponseStyle.SHORT)
        }

        // 2. Determinar Estilo
        val style = ResponseStyleEngine.determineStyle(context)
        val finalMessage = ResponseStyleEngine.formatMessage(rawMessage, style)

        // 3. Calcular Timing
        val delay = VoiceTimingManager.calculateDelay(context)
        val recommendedTime = System.currentTimeMillis() + delay

        // 4. Marcar interacción si el silencio lo permite
        if (SilenceManager.canSpeakNow()) {
            SilenceManager.markInteraction()
        }

        LogPoseLogger.i("THAMIS_VOICE_EXPERIENCE: Decision for '$rawMessage' -> ${if (shouldProceed) "SPEAK" else "SILENCE"} ($style)")

        return VoiceDecision(
            message = finalMessage,
            priority = priority,
            reason = "Validated by VoiceExperienceEngine",
            timing = recommendedTime,
            confidence = 1.0f,
            style = style
        )
    }
}
