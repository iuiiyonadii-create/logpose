package com.uriel.logpose.thamis.voice.model

import java.util.*

/**
 * Representa la entrada de voz capturada y reconocida inicialmente.
 */
data class VoiceInput(
    val text: String,
    val rawConfidence: Float,
    val timestamp: Long = System.currentTimeMillis(),
    val context: VoiceContext
)

/**
 * Representa la intención interpretada a partir de la voz.
 */
data class VoiceIntent(
    val action: String,
    val parameters: Map<String, String> = emptyMap(),
    val confidence: Float,
    val priority: Int
)

/**
 * Contexto específico para la interpretación vocal.
 */
data class VoiceContext(
    val conversationActive: Boolean = false,
    val systemState: String = "IDLE",
    val drivingSituation: String = "STOPPED",
    val noiseLevel: Float = 0f
)

/**
 * Resultado detallado del procesamiento de voz.
 */
data class VoiceRecognitionResult(
    val bestMatch: String,
    val confidenceLevel: ConfidenceLevel,
    val errors: List<String> = emptyList(),
    val suggestion: String? = null
)

enum class ConfidenceLevel {
    HIGH_CONFIDENCE,
    MEDIUM_CONFIDENCE,
    LOW_CONFIDENCE
}
