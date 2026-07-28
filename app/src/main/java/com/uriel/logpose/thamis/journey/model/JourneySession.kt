package com.uriel.logpose.thamis.journey.model

import java.util.*

/**
 * Contenedor de datos para una sesión de viaje activa.
 */
data class JourneySession(
    val id: String = UUID.randomUUID().toString(),
    val startTime: Long = System.currentTimeMillis(),
    var endTime: Long? = null,
    var startLocation: String? = null,
    var endLocation: String? = null,
    var distanceMeters: Double = 0.0,
    var maxSpeed: Float = 0f,
    var averageSpeed: Float = 0f,
    var movingTimeMs: Long = 0,
    var stoppedTimeMs: Long = 0,
    val evidences: MutableList<JourneyEvidence> = mutableListOf()
) {
    fun isFinished(): Boolean = endTime != null
}

/**
 * Estadísticas agregadas del viaje.
 */
data class JourneyStatistics(
    val totalTrips: Int,
    val totalDistance: Double,
    val totalTime: Long,
    val averageDuration: Long,
    val peakSpeed: Float
)
