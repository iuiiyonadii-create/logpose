package com.uriel.logpose.thamis_ai.decision

import com.uriel.logpose.domain.models.DrivingState

/**
 * Current environment context for decision making.
 */
data class DecisionContext(
    val drivingState: DrivingState,
    val isMusicActive: Boolean,
    val isNavigationActive: Boolean,
    val pendingNotifications: Int
)
