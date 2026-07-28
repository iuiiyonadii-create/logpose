package com.uriel.logpose.thamis_ai.analytics

/**
 * Strips private information from data before analytics processing.
 */
class PrivacyFilter {

    private val blacklistedKeys = listOf("message_content", "contact_name", "audio_path")

    fun filter(data: Map<String, Any>): Map<String, Any> {
        return data.filterKeys { it !in blacklistedKeys }
    }
}
