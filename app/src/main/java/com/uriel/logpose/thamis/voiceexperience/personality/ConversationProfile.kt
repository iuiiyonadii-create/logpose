package com.uriel.logpose.thamis.voiceexperience.personality

import com.uriel.logpose.thamis.voiceexperience.model.ResponseStyle

/**
 * FASE 25.17 — THAMIS PERSONAL AI ASSISTANT EXPERIENCE
 * FASE 7: CONVERSATION PROFILE
 */
data class ConversationProfile(
    val preferredTone: AssistantTone = AssistantTone.NATURAL,
    val preferredStyle: ResponseStyle = ResponseStyle.NORMAL,
    val isAdaptiveEnabled: Boolean = true
)
