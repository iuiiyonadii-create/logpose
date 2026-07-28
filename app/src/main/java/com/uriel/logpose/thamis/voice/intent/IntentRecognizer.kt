package com.uriel.logpose.thamis.voice.intent

import com.uriel.logpose.thamis.voice.model.VoiceIntent

/**
 * Identifica la intención a partir del texto procesado lingüísticamente.
 */
object IntentRecognizer {

    fun recognize(canonicalText: String): VoiceIntent {
        return when {
            canonicalText.contains("pone musica") || canonicalText.contains("reproducir") -> 
                VoiceIntent("PLAY_MUSIC", emptyMap(), 0.9f, 500)
            
            canonicalText.contains("llamar") -> 
                VoiceIntent("CALL_CONTACT", extractEntity(canonicalText, "llamar"), 0.85f, 700)
                
            canonicalText.contains("donde estoy") -> 
                VoiceIntent("WHERE_AM_I", emptyMap(), 0.95f, 800)
                
            canonicalText.contains("subir volumen") || canonicalText.contains("mas volumen") -> 
                VoiceIntent("INCREASE_VOLUME", emptyMap(), 0.9f, 600)
                
            else -> VoiceIntent("UNKNOWN", emptyMap(), 0.1f, 0)
        }
    }

    private fun extractEntity(text: String, action: String): Map<String, String> {
        val entity = text.substringAfter(action).trim()
        return if (entity.isNotEmpty()) mapOf("entity" to entity) else emptyMap()
    }
}
