package com.uriel.logpose.domain.learning

/**
 * Domain model for a detected behavioral pattern.
 */
data class BehaviorPattern(
    val action: String,
    val frequency: Int,
    val triggers: List<String>
)
