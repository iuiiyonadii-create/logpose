package com.uriel.logpose.data.voice

import com.uriel.logpose.domain.models.VoiceState
import com.uriel.logpose.domain.models.VoiceStatus
import com.uriel.logpose.domain.repositories.VoiceRepository
import com.uriel.logpose.features.voice.VoskVoiceEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoiceRepositoryImpl @Inject constructor(
    private val voskEngine: VoskVoiceEngine
) : VoiceRepository {
    private val _voiceState = MutableStateFlow(VoiceStatus(VoiceState.IDLE))
    override val voiceState: StateFlow<VoiceStatus> = _voiceState.asStateFlow()

    override fun startListening() {
        if (_voiceState.value.state == VoiceState.LISTENING) return
        _voiceState.value = VoiceStatus(VoiceState.LISTENING)
        voskEngine.start()
    }

    override fun stopListening() {
        voskEngine.stop()
        _voiceState.value = VoiceStatus(VoiceState.IDLE)
    }

    override fun resetStateForRestart() {
        voskEngine.stop()
        _voiceState.value = VoiceStatus(VoiceState.IDLE)
    }
}
