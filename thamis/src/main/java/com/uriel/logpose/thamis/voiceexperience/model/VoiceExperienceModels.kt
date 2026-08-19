package com.uriel.logpose.thamis.voiceexperience.model

import java.util.*

/**
 * Niveles de prioridad para la experiencia vocal de conducción.
 */
enum class VoicePriority(val level: Int) {
    EMERGENCY(1000),
    SAFETY(900),
    NAVIGATION(800),
    CALL(700),
    IMPORTANT_MESSAGE(600),
    DIALOG(500),
    MULTIMEDIA(400),
    INFORMATION(300)
}

/**
 * Estilo de respuesta de THAMIS.
 */
enum class ResponseStyle {
    SHORT,
    NORMAL,
    DETAILED,
    EMERGENCY
}

/**
 * Contexto de conducción para la toma de decisiones vocales.
 */
data class DrivingContext(
    val speedKmh: Float,
    val isNavigationActive: Boolean,
    val isCallActive: Boolean,
    val isConversationActive: Boolean,
    val cognitiveLoad: Float // 0.0 to 1.0
)

/**
 * Decisión final sobre si hablar o no.
 */
data class VoiceDecision(
    val message: String,
    val priority: VoicePriority,
    val reason: String,
    val timing: Long, // Timestamp recomendado
    val confidence: Float,
    val style: ResponseStyle
)

/**
 * Política de comportamiento vocal.
 */
data class VoicePolicy(
    val allowInterruptions: Boolean,
    val silenceWindowMs: Long,
    val maxTurns: Int
)
