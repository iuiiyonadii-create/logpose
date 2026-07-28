package com.uriel.logpose.thamis.navigation.location

/**
 * Registro auditable de una consulta de ubicación.
 */
data class LocationTrace(
    val input: String,
    val intent: LocationIntent,
    val confidence: Float,
    val gpsAvailable: Boolean,
    val accuracy: Float,
    val response: String,
    val timestamp: Long = System.currentTimeMillis()
)
