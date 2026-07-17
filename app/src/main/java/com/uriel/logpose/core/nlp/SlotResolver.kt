package com.uriel.logpose.core.nlp

object SlotResolver {

    fun resolve(

        entities: List<Entity>

    ): List<Slot> {

        return entities.map {

            Slot(

                key = it.type.name,

                value = it.value
            )
        }
    }
}