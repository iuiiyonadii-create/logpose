package com.uriel.logpose.thamis.navigation.model

/**
 * Representa el veredicto final sobre una intención de navegación.
 */
data class NavigationDecision(
    val goal: NavigationGoal,
    val destination: String,
    val confidence: Float,
    val risk: Float,
    val requiresConfirmation: Boolean,
    val reasoning: String
)
