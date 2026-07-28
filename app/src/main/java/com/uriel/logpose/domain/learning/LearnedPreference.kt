package com.uriel.logpose.domain.learning

/**
 * Domain model for a preference learned by THAMIS.
 */
data class LearnedPreference(
    val key: String,
    val value: String,
    val isAutoApply: Boolean = false
)
