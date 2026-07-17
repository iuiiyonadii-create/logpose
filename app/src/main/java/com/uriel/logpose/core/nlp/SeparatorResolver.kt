package com.uriel.logpose.core.nlp

object SeparatorResolver {

    fun split(
        text: String
    ): List<String> {

        var value = text

        LanguageManager
            .current()
            .separators
            .forEach {

                value = value.replace(
                    it,
                    "|"
                )
            }

        return value
            .split("|")
            .map { it.trim() }
            .filter { it.isNotBlank() }
    }
}