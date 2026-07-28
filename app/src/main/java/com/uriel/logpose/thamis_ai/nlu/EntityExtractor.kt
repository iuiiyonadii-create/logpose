package com.uriel.logpose.thamis_ai.nlu

import com.uriel.logpose.domain.nlu.Entity
import com.uriel.logpose.domain.nlu.EntityType

/**
 * Extracts specific variables (names, numbers) from phrases.
 */
class EntityExtractor {

    fun extract(text: String): List<Entity> {
        val entities = mutableListOf<Entity>()
        
        // Simple keyword-based extraction for MVP
        if (text.contains("llama a ")) {
            val contact = text.substringAfter("llama a ").trim()
            entities.add(Entity(EntityType.CONTACT, contact))
        }
        
        return entities
    }
}
