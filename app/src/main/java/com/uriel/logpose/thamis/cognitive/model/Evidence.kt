package com.uriel.logpose.thamis.cognitive.model

/**
 * El átomo del razonamiento de THAMIS.
 * Provee sustento matemático a las hipótesis.
 */
data class Evidence(
    val type: Type,
    val source: Source,
    val impact: Float, // +0.5, -0.2, etc.
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val expirationMs: Long // TTL para memoria volátil
) {
    enum class Type { POSITIVE, NEGATIVE, NEUTRAL }

    enum class Source {
        PHONETIC,       // Coincidencia de sonido (ALF-R)
        GRAMMAR,        // Verbos y disparadores
        CONTEXT_WORLD,  // Sensores, movimiento
        CONTEXT_SYSTEM, // Spotify sonando, bluetooth
        HISTORY,        // Memoria de trabajo
        LEARNED_HABIT   // Experiencias previas
    }

    fun isStillValid(): Boolean = (System.currentTimeMillis() - timestamp) < expirationMs
}
