package com.uriel.logpose.thamis.voiceexperience.interaction

import com.uriel.logpose.thamis.voiceexperience.model.ResponseStyle

/**
 * FASE 25.17 — THAMIS PERSONAL AI ASSISTANT EXPERIENCE
 * FASE 5: INTERACTION STYLE MANAGER
 */
object InteractionStyleManager {

    data class InteractionConfig(
        val requireVoiceConfirmation: Boolean,
        val interMessageDelayMs: Long,
        val allowInterruptions: Boolean
    )

    fun getInteractionSettings(style: ResponseStyle): InteractionConfig {
        return when (style) {
            ResponseStyle.EMERGENCY -> InteractionConfig(false, 0, false)
            ResponseStyle.SHORT -> InteractionConfig(false, 100, false)
            ResponseStyle.NORMAL -> InteractionConfig(true, 400, true)
            ResponseStyle.DETAILED -> InteractionConfig(true, 800, true)
        }
    }
}
