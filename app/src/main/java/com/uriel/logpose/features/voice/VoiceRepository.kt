package com.uriel.logpose.features.voice

import kotlinx.coroutines.flow.StateFlow

class VoiceRepository(

    private val session: VoiceSession = VoiceSession()

) {

    val state: StateFlow<VoiceState>
        get() = session.state

    fun initialize() {

        session.update(
            VoiceState.READY
        )
    }

    fun startListening() {

        session.update(
            VoiceState.LISTENING
        )
    }

    fun processing() {

        session.update(
            VoiceState.PROCESSING
        )
    }

    fun stopListening() {

        session.update(
            VoiceState.STOPPED
        )
    }

    fun error() {

        session.update(
            VoiceState.ERROR
        )
    }

    fun reset() {

        session.update(
            VoiceState.IDLE
        )
    }

    fun current(): VoiceState =
        session.current()
}