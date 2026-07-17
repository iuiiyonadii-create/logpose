package com.uriel.logpose.features.voice

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class VoiceSession {

    private val _state = MutableStateFlow(VoiceState())

    val state: StateFlow<VoiceState> = _state.asStateFlow()

    fun update(state: VoiceState) {
        _state.value = state
    }
}