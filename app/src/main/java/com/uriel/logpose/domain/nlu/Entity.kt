package com.uriel.logpose.domain.nlu

/**
 * Metadata extracted from user phrases.
 */
data class Entity(
    val type: EntityType,
    val value: String
)

enum class EntityType {
    CONTACT,
    APP_NAME,
    VOLUME_LEVEL,
    DRIVING_MODE
}
