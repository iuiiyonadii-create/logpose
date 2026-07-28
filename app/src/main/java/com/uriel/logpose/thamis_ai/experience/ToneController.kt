package com.uriel.logpose.thamis_ai.experience

/**
 * Manages the vocal tone and emotional expression (within safety boundaries).
 */
class ToneController {
    
    enum class Tone {
        DIRECT,
        NATURAL,
        FORMAL
    }

    fun getTone(): Tone = Tone.NATURAL
}
