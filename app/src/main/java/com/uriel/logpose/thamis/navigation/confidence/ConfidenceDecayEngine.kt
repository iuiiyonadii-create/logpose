package com.uriel.logpose.thamis.navigation.confidence

/**
 * Motor de decaimiento de confianza temporal.
 * Representa el hecho de que una intención humana pierde validez a medida que pasa el tiempo.
 */
object ConfidenceDecayEngine {
    
    // Cuánta confianza se pierde por segundo (0.01 = 1% por segundo)
    private const val DECAY_RATE_PER_SECOND = 0.02f
    private const val EXPIRATION_THRESHOLD_SECONDS = 20L

    /**
     * Calcula la confianza restante basada en el tiempo transcurrido.
     */
    fun calculateDecay(initialConfidence: Float, elapsedMillis: Long): Float {
        val seconds = elapsedMillis / 1000
        if (seconds >= EXPIRATION_THRESHOLD_SECONDS) return 0f
        
        val decay = seconds * DECAY_RATE_PER_SECOND
        return (initialConfidence - decay).coerceAtLeast(0f)
    }

    /**
     * Verifica si una intención ha expirado temporalmente.
     */
    fun isExpired(elapsedMillis: Long): Boolean {
        return (elapsedMillis / 1000) >= EXPIRATION_THRESHOLD_SECONDS
    }
}
