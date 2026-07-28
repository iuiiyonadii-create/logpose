package com.uriel.logpose.thamis_ai.voice

import com.uriel.logpose.domain.nlu.UserIntent

/**
 * Generates natural responses based on intent and results.
 */
class ResponseEngine {

    fun generate(intent: UserIntent): String {
        return when (intent) {
            UserIntent.PLAY_MUSIC -> "Reproduciendo música."
            UserIntent.CALL_CONTACT -> "Llamando."
            UserIntent.CHANGE_VOLUME -> "Volumen ajustado."
            UserIntent.UNKNOWN -> "¿Podrías repetirlo?"
            else -> "Entendido."
        }
    }
}
