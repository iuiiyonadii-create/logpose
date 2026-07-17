package com.uriel.logpose.features.voice

sealed interface VoiceEvent {

    data object Started : VoiceEvent

    data object Stopped : VoiceEvent

    data class CommandReceived(
        val command: String
    ) : VoiceEvent

    data class Error(
        val message: String
    ) : VoiceEvent
}