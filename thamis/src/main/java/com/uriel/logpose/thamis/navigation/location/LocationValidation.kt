package com.uriel.logpose.thamis.navigation.location

/**
 * Motor de validación de confianza para el contexto geográfico.
 */
object LocationValidation {
    fun calculateConfidence(gpsAvailable: Boolean, accuracy: Float, isMoving: Boolean): Float {
        var confidence = 0f
        if (gpsAvailable) confidence += 0.5f
        if (accuracy < 20f) confidence += 0.3f
        else if (accuracy < 50f) confidence += 0.15f
        
        if (isMoving) confidence += 0.2f else confidence += 0.1f
        
        return confidence.coerceIn(0f, 1f)
    }
}
