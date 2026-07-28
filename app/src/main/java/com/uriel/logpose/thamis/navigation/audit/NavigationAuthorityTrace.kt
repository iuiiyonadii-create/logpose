package com.uriel.logpose.thamis.navigation.audit

import com.uriel.logpose.thamis.navigation.provider.NavigationProviderType

/**
 * Traza detallada de una decisión de autoridad para navegación.
 */
data class NavigationAuthorityTrace(
    val authorityGranted: Boolean,
    val authorityDenied: Boolean,
    val safetyDecision: String,
    val confidence: Float,
    val destination: String,
    val provider: NavigationProviderType,
    val executionTimeMs: Long,
    val validatorResult: String,
    val reason: String,
    val timestamp: Long = System.currentTimeMillis()
)
