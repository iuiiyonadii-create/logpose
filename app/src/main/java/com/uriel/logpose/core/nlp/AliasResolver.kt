package com.uriel.logpose.core.nlp

object AliasResolver {

    fun resolve(
        word: String
    ): String {

        return LanguageManager
            .current()
            .aliases[word]
            ?: word
    }
}