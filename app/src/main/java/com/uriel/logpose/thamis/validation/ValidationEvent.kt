package com.uriel.logpose.thamis.validation

import com.uriel.logpose.thamis.cognitive.model.ThamisDecision
import com.uriel.logpose.thamis.cognitive.model.WorldState
import com.uriel.logpose.thamis.shadow.ShadowResult
import com.thamis.lab.core.contracts.intent.Intent

/**
 * Representa un evento de interacción individual durante una sesión de validación.
 */
data class ValidationEvent(
    val timestamp: Long = System.currentTimeMillis(),
    val rawInput: String,
    val normalizedText: String,
    val worldState: WorldState,
    val thamisDecision: ThamisDecision,
    val legacyIntent: Intent,
    val shadowResult: ShadowResult
)
