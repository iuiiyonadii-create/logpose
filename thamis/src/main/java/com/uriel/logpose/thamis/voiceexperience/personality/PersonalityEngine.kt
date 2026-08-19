package com.uriel.logpose.thamis.voiceexperience.personality

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.voiceexperience.model.DrivingContext
import com.uriel.logpose.thamis.voiceexperience.model.ResponseStyle

/**
 * FASE 25.17 — THAMIS PERSONAL AI ASSISTANT EXPERIENCE
 * FASE 2: PERSONALITY ENGINE
 */
object PersonalityEngine {

    /**
     * Resuelve el estilo de respuesta basado en el contexto de conducción.
     */
    fun resolveResponseStyle(context: DrivingContext): ResponseStyle {
        return when {
            context.cognitiveLoad > 0.8f || context.speedKmh > 100f -> ResponseStyle.EMERGENCY
            context.cognitiveLoad > 0.5f || context.speedKmh > 60f -> ResponseStyle.SHORT
            context.isCallActive -> ResponseStyle.SHORT
            context.isNavigationActive -> ResponseStyle.NORMAL
            else -> ResponseStyle.DETAILED
        }
    }

    /**
     * Aplica el estilo al texto.
     */
    fun applyStyle(text: String, style: ResponseStyle): String {
        return when (style) {
            ResponseStyle.SHORT -> text.substringBefore(".").trim()
            ResponseStyle.EMERGENCY -> "¡Atención! ${text.substringBefore(".")}"
            ResponseStyle.DETAILED -> "THAMIS: $text"
            else -> text
        }
    }

    /**
     * FASE 8: CONTEXTUAL PERSONALITY
     */
    fun generateFinalResponse(
        rawMessage: String,
        context: DrivingContext,
        profile: ConversationProfile
    ): String {
        val style = if (profile.isAdaptiveEnabled) {
            resolveResponseStyle(context)
        } else {
            profile.preferredStyle
        }

        val stylizedText = applyStyle(rawMessage, style)
        return ToneController.adapt(stylizedText, profile.preferredTone)
    }
}
