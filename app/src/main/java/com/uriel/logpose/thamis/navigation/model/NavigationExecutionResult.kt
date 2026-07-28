package com.uriel.logpose.thamis.navigation.model

import com.uriel.logpose.thamis.navigation.provider.NavigationProviderType
import com.uriel.logpose.thamis.intent.Intent

/**
 * Resultado de una ejecución de comando de navegación.
 */
data class NavigationExecutionResult(
    val provider: NavigationProviderType,
    val executionTimeMs: Long,
    val success: Boolean,
    val reason: String,
    val safetyDecision: String,
    val confidence: Float,
    val authorityDecision: String,
    val destination: String,
    val routeIntent: Intent
)
