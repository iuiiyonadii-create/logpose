package com.uriel.logpose.thamis.field_test.model

import java.util.*

/**
 * Sesión de prueba de campo real con un conductor.
 */
data class TestSession(
    val id: String = UUID.randomUUID().toString(),
    val testerName: String,
    val startTime: Long = System.currentTimeMillis(),
    var endTime: Long? = null,
    val deviceModel: String,
    val appVersion: String,
    val scenario: String,
    val conditions: Map<String, String>,
    val events: MutableList<FieldEvent> = mutableListOf()
)

/**
 * Evento registrado durante una prueba de campo.
 */
data class FieldEvent(
    val timestamp: Long = System.currentTimeMillis(),
    val type: String, // VOICE, BT_DROP, NAV_ERROR, USER_FEEDBACK
    val description: String,
    val severity: String // INFO, WARN, ERROR, CRITICAL
)

/**
 * Perfil del participante (Tester).
 */
data class TesterProfile(
    val id: String,
    val experienceLevel: String, // BEGINNER, EXPERT
    val motorcycleType: String,
    val intercomModel: String
)

/**
 * Informe final consolidado de una prueba de campo.
 */
data class FieldTestReport(
    val sessionId: String,
    val summary: String,
    val issuesFound: List<String>,
    val safetyScore: Float,
    val userSatisfaction: Int // 1-5
)
