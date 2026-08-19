package com.uriel.logpose.thamis.cognitive.model

/**
 * Representa el costo de un error.
 * La confianza necesaria de THAMIS es proporcional al riesgo.
 */
data class Risk(
    val type: Type,
    val level: Float, // 0.0 (Sin riesgo) a 1.0 (Crítico)
    val mitigationStrategy: Strategy
) {
    enum class Type {
        PHYSICAL, // Seguridad del conductor
        SYSTEM,   // Integridad de datos/apps
        SOCIAL    // Mensaje/Llamada errónea
    }

    enum class Strategy {
        SILENT_EXECUTION,   // Ejecutar sin hablar (Bip)
        REQUEST_CONFIRMATION, // Preguntar "¿Querías...?"
        FORCE_REJECTION      // Entendido pero bloqueado por seguridad
    }
}
