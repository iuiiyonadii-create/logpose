package com.uriel.logpose.domain.context

import com.uriel.logpose.domain.models.DrivingState

/**
 * Domain model representing the full driving environment.
 */
data class DrivingContext(
    val state: DrivingState,
    val speed: Float = 0.0f,
    val isNearDestination: Boolean = false,
    val attentionRequired: AttentionLevel = AttentionLevel.NORMAL
)

enum class AttentionLevel {
    LOW,
    NORMAL,
    HIGH,
    CRITICAL
}
