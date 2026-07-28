package com.uriel.logpose.thamis.cognitive.disambiguation

/**
 * Representa una posible interpretación de una entidad encontrada en el texto.
 */
data class EntityCandidate(
    val entityName: String,
    val entityType: Type,
    val confidence: Float,
    val source: String
) {
    enum class Type {
        ARTIST,
        SONG,
        APP,
        CONTACT,
        PLACE,
        OTHER
    }
}
