package com.uriel.logpose.thamis.trust.model

import java.util.*

/**
 * Explicación humanizada de una decisión tomada por THAMIS.
 */
data class DecisionExplanation(
    val decisionId: String,
    val summary: String,
    val detailedReason: String,
    val dataSources: List<String>,
    val confidence: Float
)

/**
 * Registro técnico del razonamiento.
 */
data class ReasoningTrace(
    val event: String,
    val contextSnapshotId: String?,
    val rulesApplied: List<String>,
    val finalDecision: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Informe sobre la confiabilidad de un dato.
 */
data class ConfidenceReport(
    val score: Float,
    val source: String,
    val limitingFactors: List<String>
)

/**
 * Configuración de transparencia para el usuario.
 */
data class UserControlSetting(
    val featureName: String,
    val isEnabled: Boolean,
    val description: String
)
