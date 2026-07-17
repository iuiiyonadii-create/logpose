package com.uriel.logpose.features.voice

class VoiceRepository(
    private val session: VoiceSession
) {

    val state = session.state

    fun initialize() {
        session.update(
            VoiceState(
                initialized = true
            )
        )
    }
}