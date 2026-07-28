package com.uriel.logpose.thamis.contracts

/**
 * Representa la entrada cruda del sistema de voz.
 */
data class SpeechInput(
    val rawText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val source: Source = Source.VOSK,
    val confidence: Float = 1.0f
) {
    enum class Source {
        VOSK,
        MANUAL_GOOGLE,
        REMOTE_BRIDGE
    }
}
