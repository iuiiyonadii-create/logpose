package com.uriel.logpose.thamis.interaction

import java.util.*

/**
 * Representa una solicitud de interacción desde cualquier dominio hacia el usuario.
 */
data class InteractionRequest(
    val id: String = UUID.randomUUID().toString(),
    val domain: Domain,
    val priority: InteractionPriority,
    val payload: Any, // Texto a hablar, comando a ejecutar, etc.
    val timestamp: Long = System.currentTimeMillis(),
    val expiryMs: Long = 60_000L, // Tiempo tras el cual el evento ya no es relevante
    val metadata: Map<String, Any> = emptyMap()
) {
    enum class Domain {
        NAVIGATION, MULTIMEDIA, COMMUNICATION, NOTIFICATION, SYSTEM, EMERGENCY, SENSOR
    }

    fun isExpired(): Boolean = System.currentTimeMillis() - timestamp > expiryMs
}
