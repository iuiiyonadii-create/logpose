package com.uriel.logpose.thamis_ai.context

/**
 * Handles external environment context like weather or time of day.
 */
data class EnvironmentContext(
    val weatherCondition: String = "Clear",
    val isDaylight: Boolean = true
)
