package com.uriel.logpose.thamis.safety.model

import java.util.*

/**
 * Representa el nivel de riesgo físico actual de la conducción.
 */
enum class RiskLevel {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

/**
 * Representa el estado de atención estimada del conductor.
 */
enum class AttentionState {
    AVAILABLE,
    BUSY,
    HIGH_LOAD,
    UNKNOWN
}

/**
 * Contexto detallado de la situación de conducción.
 */
data class RidingContext(
    val speedKmh: Float,
    val isNavigationComplex: Boolean,
    val isCallActive: Boolean,
    val activeEventsCount: Int,
    val estimatedCognitiveLoad: Float // 0.0 to 1.0
)

/**
 * Evaluación consolidada de seguridad.
 */
data class SafetyAssessment(
    val risk: RiskLevel,
    val reason: String,
    val confidence: Float,
    val recommendation: SafetyAction
)

enum class SafetyAction {
    ALLOW,
    DELAY,
    SIMPLIFY,
    CANCEL,
    ESCALATE
}
