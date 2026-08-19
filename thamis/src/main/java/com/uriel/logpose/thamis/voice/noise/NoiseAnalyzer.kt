package com.uriel.logpose.thamis.voice.noise

/**
 * Arquitectura para el análisis de ruido ambiental (viento, motor, casco).
 */
object NoiseAnalyzer {

    fun analyzeRisk(speed: Float, environmentNoise: Float): Float {
        // Cuanto mayor es la velocidad, mayor es la probabilidad de ruido de viento.
        // Retorna un score de riesgo de 0.0 a 1.0.
        var risk = environmentNoise
        if (speed > 80f) risk += 0.3f
        if (speed > 110f) risk += 0.5f
        
        return risk.coerceIn(0f, 1f)
    }
}
