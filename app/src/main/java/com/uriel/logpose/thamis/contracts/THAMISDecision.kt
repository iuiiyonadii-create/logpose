package com.uriel.logpose.thamis.contracts

import com.uriel.logpose.thamis.intent.Intent

/**
 * Veredicto final del cerebro THAMIS.
 */
data class THAMISDecision(
    val type: Type,
    val intent: Intent,
    val entities: Map<String, String> = emptyMap(),
    val finalConfidence: Float,
    val reason: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    enum class Type {
        EXECUTE,    // Ejecución directa
        CONFIRM,    // Requiere preguntar "¿Querías...?"
        IGNORE,     // No estamos seguros, silencio total
        DENY        // Entendido pero prohibido por política de seguridad
    }
}
