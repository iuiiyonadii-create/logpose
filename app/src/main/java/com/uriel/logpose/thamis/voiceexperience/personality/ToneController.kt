package com.uriel.logpose.thamis.voiceexperience.personality

/**
 * FASE 25.17 — THAMIS PERSONAL AI ASSISTANT EXPERIENCE
 * FASE 4: TONE CONTROLLER
 */
enum class AssistantTone {
    FORMAL,
    NATURAL,
    DIRECT
}

object ToneController {

    fun adapt(message: String, tone: AssistantTone): String {
        return when (tone) {
            AssistantTone.FORMAL -> "Confirmado: $message"
            AssistantTone.DIRECT -> message.removePrefix("Entendido, ").replaceFirstChar { it.uppercase() }
            AssistantTone.NATURAL -> message
        }
    }
}
