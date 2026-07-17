package com.uriel.logpose.core.nlp

object NaturalLanguageEngine {

    fun process(

        text: String

    ): NlpResult {

        NlpStatistics.register()

        val intent =
            IntentClassifier.classify(text)

        val entities =
            EntityExtractor.extract(text)

        val slots =
            SlotResolver.resolve(
                entities
            )

        return NlpResult(

            intent = intent,

            entities = entities,

            slots = slots,

            confidence =
                if (intent == Intent.UNKNOWN)
                    0f
                else
                    1f
        )
    }
}