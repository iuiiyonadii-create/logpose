package com.uriel.logpose.domain.models

import com.thamis.lab.core.contracts.command.LogPoseCommand

/**
 * Domain model representing the voice recognition state.
 */
enum class VoiceState {
    IDLE,
    LISTENING,
    PROCESSING,
    ERROR
}

data class VoiceStatus(
    val state: VoiceState,
    val partialText: String = "",
    val finalCommand: LogPoseCommand? = null,
    val errorMessage: String? = null
)
