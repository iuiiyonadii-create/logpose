package com.uriel.logpose.thamis.model

import com.uriel.logpose.thamis.intent.Intent
import com.uriel.logpose.thamis.evidence.Evidence
import java.util.UUID

/**
 * Representa una posible interpretación de la intención del usuario basada en evidencias.
 * Modelo unificado de THAMIS.
 */
data class Hypothesis(
    val id: UUID = UUID.randomUUID(),
    val intent: Intent,
    val evidences: List<Evidence>,
    val score: HypothesisScore,
    val source: String
)

/**
 * Calidad de una hipótesis.
 */
data class HypothesisScore(
    val confidence: Float,
    val explanation: String
)
