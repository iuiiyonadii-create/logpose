package com.uriel.logpose.core.parser.intent

import com.uriel.logpose.core.parser.confidence.ConfidenceEngine

object IntentResolver {

    fun resolve(
        text: String
    ): IntentResult {

        val confidence = ConfidenceEngine.evaluate(text)

        return IntentResult(
            text = confidence.text,
            confidence = confidence.confidence
        )
    }
}