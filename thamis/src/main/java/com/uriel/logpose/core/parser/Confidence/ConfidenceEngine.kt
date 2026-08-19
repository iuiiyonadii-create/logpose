package com.uriel.logpose.core.parser.confidence

object ConfidenceEngine {

    private const val DEFAULT_CONFIDENCE = 1.0f
    private const val MIN_TEXT_SIZE = 2

    fun evaluate(text: String): ConfidenceResult {

        val confidence =
            if (text.length < MIN_TEXT_SIZE)
                0.0f
            else
                DEFAULT_CONFIDENCE

        return ConfidenceResult(
            text = text,
            confidence = confidence
        )
    }
}