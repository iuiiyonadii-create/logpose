package com.uriel.logpose.domain.repositories

import com.uriel.logpose.domain.models.VoiceStatus
import kotlinx.coroutines.flow.StateFlow

/**
 * Contract for Voice Command operations.
 */
interface VoiceRepository {
    val voiceState: StateFlow<VoiceStatus>
    fun startListening()
    fun stopListening()
    fun resetStateForRestart()
}
