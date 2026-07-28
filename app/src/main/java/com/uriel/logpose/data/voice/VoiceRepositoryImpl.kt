package com.uriel.logpose.data.voice

import com.uriel.logpose.domain.models.VoiceState
import com.uriel.logpose.domain.models.VoiceStatus
import com.uriel.logpose.domain.repositories.VoiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class VoiceRepositoryImpl @Inject constructor() : VoiceRepository {
    private val _voiceState = MutableStateFlow(VoiceStatus(VoiceState.IDLE))
    override val voiceState: StateFlow<VoiceStatus> = _voiceState.asStateFlow()

    override fun startListening() {
        _voiceState.value = VoiceStatus(VoiceState.LISTENING)
    }

    override fun stopListening() {
        _voiceState.value = VoiceStatus(VoiceState.IDLE)
    }

    override fun resetStateForRestart() {
        _voiceState.value = VoiceStatus(VoiceState.IDLE)
    }
}
