package com.uriel.logpose.core.nlp

object LanguageManager {

    private var language = Language()

    fun load(
        value: Language
    ) {

        language = value
    }

    fun current(): Language =
        language
}