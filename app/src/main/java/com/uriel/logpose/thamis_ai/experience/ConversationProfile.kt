package com.uriel.logpose.thamis_ai.experience

/**
 * Tracks interaction metrics to refine the conversation style.
 */
data class ConversationProfile(
    val averageTurnLength: Int = 0,
    val preferredLanguage: String = "es",
    val interactionFrequency: Int = 0
)
