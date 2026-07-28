package com.uriel.logpose.thamis.voiceexperience.timing

import com.uriel.logpose.thamis.voiceexperience.model.DrivingContext

/**
 * Determina el momento exacto para emitir un mensaje vocal.
 */
object VoiceTimingManager {

    fun calculateDelay(context: DrivingContext): Long {
        // Si el usuario está hablando, esperar al menos 2 segundos.
        if (context.isConversationActive) return 2000L

        // A alta velocidad, añadir un pequeño buffer para que el usuario procese.
        if (context.speedKmh > 100f) return 500L

        return 0L
    }
}
