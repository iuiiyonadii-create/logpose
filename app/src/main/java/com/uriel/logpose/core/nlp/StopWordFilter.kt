package com.uriel.logpose.core.nlp

object StopWordFilter {

    fun filter(
        words: List<String>
    ): List<String> {

        val stopWords =
            LanguageManager.current().stopWords

        return words.filter {

            it !in stopWords
        }
    }
}