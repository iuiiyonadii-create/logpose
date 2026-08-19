package com.uriel.logpose.thamis.decision

import com.thamis.lab.core.contracts.intent.Intent

/**
 * Representa una posible interpretación de la intención del usuario.
 */
data class Hypothesis(
    val intent: Intent,
    val entities: Map<String, String>,
    val score: Float,
    val source: String
)
