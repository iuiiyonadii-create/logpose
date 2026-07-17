package com.uriel.logpose.core.nlp

object NlpEngine {

    fun execute(
        text: String
    ): NlpResult {

        return NaturalLanguageEngine
            .process(text)
    }
}