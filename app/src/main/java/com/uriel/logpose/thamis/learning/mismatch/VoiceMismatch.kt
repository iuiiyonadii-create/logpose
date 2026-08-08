package com.uriel.logpose.thamis.learning.mismatch

import com.thamis.lab.core.contracts.intent.Intent

/**
 * Registra un evento donde el texto de Vosk no coincide con la expectativa del usuario.
 */
data class VoiceMismatch(
    val rawAudioId: String,
    val expectedText: String,
    val voskText: String,
    val normalizedText: String,
    val entityFound: String?,
    val intent: Intent,
    val confidence: Float,
    val type: MismatchType,
    val result: Result,
    val timestamp: Long = System.currentTimeMillis()
) {
    enum class Result { CORRECTED, FAILED }
}
