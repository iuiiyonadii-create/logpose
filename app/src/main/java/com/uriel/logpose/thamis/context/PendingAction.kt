package com.uriel.logpose.thamis.context

import java.util.*

/**
 * Representa una acción que requiere confirmación o entrada adicional del usuario.
 */
data class PendingAction(
    val id: String = UUID.randomUUID().toString(),
    val domain: FocusDomain,
    val actionType: String,
    val payload: Any,
    val timestamp: Long = System.currentTimeMillis(),
    val expiryMs: Long = 20_000L // Por defecto 20 segundos para responder "Sí/No"
) {
    fun isExpired(): Boolean = System.currentTimeMillis() - timestamp > expiryMs
}

/**
 * Tiempos de vida configurables para diferentes tipos de datos en el contexto.
 */
object ContextLifetime {
    const val CONVERSATION_MS = 20_000L
    const val DESTINATION_MS = 300_000L // 5 minutos
    const val SONG_INFO_MS = 120_000L    // 2 minutos
    const val CONFIRMATION_MS = 20_000L
    const val SNAPSHOT_LIMIT = 50
}
