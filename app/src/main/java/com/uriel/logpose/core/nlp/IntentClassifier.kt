package com.uriel.logpose.core.nlp

object IntentClassifier {

    fun classify(

        text: String

    ): Intent {

        val intents =
            LanguageRepository.intents()

        val keys =
            intents.keys()

        while (keys.hasNext()) {

            val key =
                keys.next()

            val values =
                intents.getJSONArray(key)

            for (i in 0 until values.length()) {

                val value =
                    values.getString(i)

                if (
                    text.contains(
                        value,
                        true
                    )
                ) {

                    return try {

                        Intent.valueOf(
                            key.uppercase()
                        )

                    } catch (_: Exception) {

                        Intent.UNKNOWN
                    }
                }
            }
        }

        return Intent.UNKNOWN
    }
}