package com.uriel.logpose.thamis.journeyintelligence.model

import java.util.*

/**
 * Sesión de viaje enriquecida con inteligencia.
 */
data class JourneySession(
    val id: String = UUID.randomUUID().toString(),
    val startTime: Long = System.currentTimeMillis(),
    var endTime: Long? = null,
    val context: JourneyContext,
    val events: List<String> = emptyList(),
    val results: Map<String, Any> = emptyMap()
)

/**
 * Contexto semántico del viaje.
 */
data class JourneyContext(
    val type: JourneyType,
    val objective: String,
    val currentState: String,
    val userNeeds: List<String> = emptyList()
)

enum class JourneyType { DAILY_COMMUTE, WORK_ROUTE, DELIVERY, LONG_TRIP, UNKNOWN_ROUTE }

/**
 * Patrón recurrente detectado por el motor.
 */
data class JourneyPattern(
    val id: String = UUID.randomUUID().toString(),
    val patternName: String,
    val frequency: Int,
    val confidence: Float // 0.0 to 1.0
)

/**
 * Sugerencia proactiva generada por THAMIS.
 */
data class JourneyInsight(
    val observation: String,
    val benefit: String,
    val recommendation: String
)
