package com.uriel.logpose.thamis.voicevalidation.noise

import com.uriel.logpose.thamis.voicevalidation.model.NoiseProfile

/**
 * Prepara y simula perfiles de ruido para pruebas de estrés vocal.
 */
object NoiseSimulationEngine {

    private val profiles = listOf(
        NoiseProfile("Moto Encendida", 0.4f, 0.2f),
        NoiseProfile("Viento Ciudad", 0.6f, 0.4f),
        NoiseProfile("Viento Ruta", 0.9f, 0.8f),
        NoiseProfile("Tráfico Pesado", 0.7f, 0.5f),
        NoiseProfile("Casco Abierto", 0.8f, 0.7f),
        NoiseProfile("Casco Cerrado", 0.3f, 0.1f)
    )

    fun getProfile(type: String): NoiseProfile? {
        return profiles.find { it.type == type }
    }

    /**
     * Calcula la degradación teórica de la confianza basada en el ruido.
     */
    fun calculateConfidencePenalty(profile: NoiseProfile): Float {
        return profile.impactScore * 0.3f
    }
}
