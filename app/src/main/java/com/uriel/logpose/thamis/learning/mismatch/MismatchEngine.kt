package com.uriel.logpose.thamis.learning.mismatch

import com.uriel.logpose.thamis.intent.Intent
import com.uriel.logpose.thamis.language.PhoneticEngine

/**
 * Motor de detección y aprendizaje de divergencias fonéticas.
 * Identifica cuándo Vosk alucina y cómo THAMIS debe corregirlo.
 */
object MismatchEngine {

    /**
     * Analiza el mismatch y decide si se puede corregir.
     */
    fun process(expected: String, voskOutput: String, currentIntent: Intent): VoiceMismatch {
        val distance = PhoneticEngine.similarity(expected, voskOutput)
        
        // Determinamos el tipo de error
        val type = when {
            distance > 0.7f -> MismatchType.PHONETIC_ERROR
            expected.contains("musica") || expected.contains("pone") -> MismatchType.INTENT_ERROR
            else -> MismatchType.ENTITY_ERROR
        }

        val isCorrected = distance > 0.6f // Umbral de corrección automática

        return VoiceMismatch(
            rawAudioId = "REC_${System.currentTimeMillis()}",
            expectedText = expected,
            voskText = voskOutput,
            normalizedText = if (isCorrected) expected else voskOutput,
            entityFound = if (isCorrected) expected else null,
            intent = currentIntent,
            confidence = distance,
            type = type,
            result = if (isCorrected) VoiceMismatch.Result.CORRECTED else VoiceMismatch.Result.FAILED
        )
    }
}
