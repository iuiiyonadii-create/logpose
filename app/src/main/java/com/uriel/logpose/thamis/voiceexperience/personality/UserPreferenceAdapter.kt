package com.uriel.logpose.thamis.voiceexperience.personality

import com.uriel.logpose.domain.settings.SettingsStore
import com.uriel.logpose.thamis.voiceexperience.model.ResponseStyle

/**
 * FASE 25.17 — THAMIS PERSONAL AI ASSISTANT EXPERIENCE
 * FASE 6: USER PREFERENCE ADAPTER
 */
class UserPreferenceAdapter(private val settings: SettingsStore) {

    companion object {
        private const val PREFIX = "thamis_vxp_"
        private const val KEY_TONE = "${PREFIX}tone"
        private const val KEY_STYLE = "${PREFIX}style"
        private const val KEY_ADAPTIVE = "${PREFIX}adaptive"
    }

    fun loadProfile(): ConversationProfile {
        val toneStr = settings.getString(KEY_TONE, AssistantTone.NATURAL.name) ?: AssistantTone.NATURAL.name
        val styleStr = settings.getString(KEY_STYLE, ResponseStyle.NORMAL.name) ?: ResponseStyle.NORMAL.name
        
        return ConversationProfile(
            preferredTone = safeValueOf(toneStr, AssistantTone.NATURAL),
            preferredStyle = safeValueOf(styleStr, ResponseStyle.NORMAL),
            isAdaptiveEnabled = settings.getBoolean(KEY_ADAPTIVE, true)
        )
    }

    private inline fun <reified T : Enum<T>> safeValueOf(name: String, default: T): T {
        return try {
            java.lang.Enum.valueOf(T::class.java, name)
        } catch (e: Exception) {
            default
        }
    }
}
