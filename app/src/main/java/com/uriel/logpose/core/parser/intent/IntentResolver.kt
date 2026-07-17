package com.uriel.logpose.core.parser.intent

object IntentResolver {

    fun resolve(
        text: String
    ): IntentResult {

        return IntentResult(
            text = text,
            confidence = 1.0f
        )
    }
}