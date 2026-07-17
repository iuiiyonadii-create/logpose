package com.uriel.logpose.core.nlp

object PhraseMatcher {

    fun match(

        text: String

    ): String? {

        val phrases =
            LanguageRepository.phrases()

        val keys = phrases.keys()

        while (keys.hasNext()) {

            val key = keys.next()

            val values =
                phrases.getJSONArray(key)

            for (i in 0 until values.length()) {

                val phrase =
                    values.getString(i)

                if (
                    text.contains(
                        phrase,
                        true
                    )
                ) {
                    return key
                }
            }
        }

        return null
    }
}