package com.uriel.logpose.thamis_ai.nlu

import com.uriel.logpose.domain.nlu.UserIntent

/**
 * Classifies a normalized string into a domain intent.
 */
class IntentClassifier {

    fun classify(text: String): UserIntent {
        return when {
            text.contains("reproducir") || text.contains("pon") -> UserIntent.PLAY_MUSIC
            text.contains("para") || text.contains("detener") || text.contains("pausa") -> UserIntent.PAUSE_MUSIC
            text.contains("volumen") || text.contains("fuerte") || text.contains("bajo") -> UserIntent.CHANGE_VOLUME
            text.contains("llama") -> UserIntent.CALL_CONTACT
            text.contains("lee") || text.contains("mensajes") -> UserIntent.READ_MESSAGE
            else -> UserIntent.UNKNOWN
        }
    }
}
