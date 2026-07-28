package com.uriel.logpose.thamis_ai.developer_platform.api

/**
 * Specifically for adding new voice-triggerable behaviors.
 */
interface VoiceExtensionAPI {
    fun registerKeywords(keywords: List<String>)
    fun handleVoiceIntent(intent: String)
}
