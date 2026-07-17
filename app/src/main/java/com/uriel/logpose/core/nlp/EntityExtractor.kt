package com.uriel.logpose.core.nlp

object EntityExtractor {

    fun extract(

        text: String

    ): List<Entity> {

        val result =
            mutableListOf<Entity>()

        val entities =
            LanguageRepository.entities()

        val keys =
            entities.keys()

        while (keys.hasNext()) {

            val key =
                keys.next()

            val values =
                entities.getJSONArray(key)

            for (i in 0 until values.length()) {

                val value =
                    values.getString(i)

                if (
                    text.contains(
                        value,
                        true
                    )
                ) {

                    result.add(

                        Entity(

                            value = value,

                            type = EntityType.UNKNOWN
                        )
                    )
                }
            }
        }

        return result
    }
}