package com.uriel.logpose.thamis.personalization.model

import java.util.*

/**
 * Perfil maestro del usuario.
 */
data class UserProfile(
    val userId: String = "rider_01",
    val config: Map<String, String> = emptyMap(),
    val preferences: List<UserPreference> = emptyList(),
    val communicationStyle: String = "NORMAL"
)

/**
 * Representa una preferencia individual del usuario.
 */
data class UserPreference(
    val type: PreferenceType,
    val value: String,
    val confidence: Float,
    val lastUpdated: Long = System.currentTimeMillis()
)

enum class PreferenceType {
    VOICE_STYLE,
    MESSAGE_STYLE,
    NOTIFICATION_LEVEL,
    MUSIC_BEHAVIOR,
    NAVIGATION_STYLE
}

/**
 * Registro de un cambio en las preferencias.
 */
data class PreferenceChange(
    val type: PreferenceType,
    val oldValue: String?,
    val newValue: String,
    val reason: String,
    val confirmedByUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Regla de personalización aprendida o predefinida.
 */
data class PersonalizationRule(
    val id: String = UUID.randomUUID().toString(),
    val condition: String,
    val behavior: String,
    val priority: Int
)
