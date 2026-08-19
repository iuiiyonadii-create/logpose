package com.uriel.logpose.thamis.navigation.audit

/**
 * Registra el pensamiento forense de THAMIS relacionado con decisiones de navegación.
 * Permite reconstruir por qué se autorizó o bloqueó una ruta.
 */
data class NavigationTrace(
    val id: String,
    val input: String,
    val goal: String,
    val intentCategory: String,
    val destination: String?,
    val destinationConfidence: Float,
    val memoryBoost: Float,
    val confidenceDecay: Float,
    val finalConfidence: Float,
    val expired: Boolean,
    val learningContribution: String?,
    val evidences: List<String>,
    val confidence: Float,
    val speedKmh: Int,
    val gpsAvailable: Boolean,
    val decision: String,
    val timestamp: Long = System.currentTimeMillis()
)
