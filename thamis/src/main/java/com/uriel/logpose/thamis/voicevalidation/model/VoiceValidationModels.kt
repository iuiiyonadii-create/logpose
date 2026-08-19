package com.uriel.logpose.thamis.voicevalidation.model

import java.util.*

/**
 * Representa un ejemplo de voz para entrenamiento o validación.
 */
data class VoiceSample(
    val id: String = UUID.randomUUID().toString(),
    val phrase: String,
    val expectedIntent: String,
    val variation: String, // argentino, rioplatense, informal, etc.
    val context: String,
    val result: String? = null
)

/**
 * Evaluación de un intento de reconocimiento.
 */
data class RecognitionEvaluation(
    val inputPhrase: String,
    val expectedResult: String,
    val actualResult: String,
    val precision: Float, // 0.0 to 1.0
    val confidence: Float
)

/**
 * Perfil de ruido para simular condiciones de conducción.
 */
data class NoiseProfile(
    val type: String, // Viento, Motor, Casco, etc.
    val intensity: Float, // 0.0 to 1.0
    val impactScore: Float // Impacto estimado en la comprensión
)

/**
 * Informe consolidado de precisión vocal.
 */
data class VoiceAccuracyReport(
    val generalAccuracy: Float,
    val frequentErrors: List<String>,
    val neededImprovements: List<String>,
    val timestamp: Long = System.currentTimeMillis()
)
