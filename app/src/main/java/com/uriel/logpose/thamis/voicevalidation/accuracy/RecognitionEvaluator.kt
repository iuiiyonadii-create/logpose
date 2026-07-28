package com.uriel.logpose.thamis.voicevalidation.accuracy

import com.uriel.logpose.thamis.voicevalidation.model.RecognitionEvaluation
import com.uriel.logpose.thamis.voicevalidation.phonetic.PhoneticValidationEngine

/**
 * Evaluador de precisión de reconocimiento de voz.
 */
object RecognitionEvaluator {

    fun evaluate(input: String, expected: String, actual: String, confidence: Float): RecognitionEvaluation {
        val inputCanonical = PhoneticValidationEngine.validateSlang(input)
        val expectedCanonical = PhoneticValidationEngine.validateSlang(expected)
        val actualCanonical = PhoneticValidationEngine.validateSlang(actual)

        val precision = if (expectedCanonical == actualCanonical) 1.0f else 0.5f // Simplificado v1.0

        return RecognitionEvaluation(
            inputPhrase = input,
            expectedResult = expected,
            actualResult = actual,
            precision = precision,
            confidence = confidence
        )
    }
}
