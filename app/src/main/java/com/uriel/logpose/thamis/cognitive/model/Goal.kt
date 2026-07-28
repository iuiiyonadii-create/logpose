package com.uriel.logpose.thamis.cognitive.model

/**
 * Representa el propósito real del usuario. 
 * THAMIS no piensa en comandos técnicos, sino en estados finales deseados.
 */
data class Goal(
    val category: Category,
    val priority: Float, // 0.0 a 1.0
    val parameters: Map<String, String>,
    val targetState: String
) {
    enum class Category {
        COMMUNICATION, // Llamar, Mensaje
        MULTIMEDIA,    // Música, Volumen
        NAVIGATION,    // GPS, Destinos
        SYSTEM_NEED,   // Batería, Estado, Privacidad
        UNKNOWN
    }

    enum class NavigationTarget {
        ADDRESS,
        CONTACT,
        FAVORITE_PLACE,
        COORDINATES,
        UNKNOWN
    }
}
