package com.uriel.logpose.thamis.decision

/**
 * Representa una pieza de evidencia que soporta o debilita una hipótesis.
 */
data class Evidence(
    val type: Type,
    val weight: Float,
    val description: String
) {
    enum class Type {
        PHONETIC_MATCH,    // Coincidencia por sonido (ALF-R)
        GRAMMAR_MATCH,     // Coincidencia por triggers (pone, reproducir)
        ENTITY_MATCH,      // Encontrado en base de datos de artistas/contactos
        CONTEXT_BOOST,     // Mejora por estado actual (música sonando)
        HISTORY_MATCH      // Repetición de intención anterior
    }
}
