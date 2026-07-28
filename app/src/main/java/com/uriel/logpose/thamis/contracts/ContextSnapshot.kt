package com.uriel.logpose.thamis.contracts

import com.uriel.logpose.thamis.intent.Intent

/**
 * La foto del mundo exterior que THAMIS usa para decidir.
 */
data class ContextSnapshot(
    val driving: DrivingState,
    val media: MediaState,
    val history: ConversationState
) {
    data class DrivingState(
        val isMoving: Boolean = false,
        val speedKmh: Int = 0,
        val hasActiveNavigation: Boolean = false,
        val hasActiveCall: Boolean = false
    )

    data class MediaState(
        val isMusicPlaying: Boolean = false,
        val currentAppName: String? = null,
        val currentTrackName: String? = null
    )

    data class ConversationState(
        val lastIntent: Intent = Intent.UNKNOWN,
        val lastEntity: String? = null,
        val timestamp: Long = 0L
    )
}
