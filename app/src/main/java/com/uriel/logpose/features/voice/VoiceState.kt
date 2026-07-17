package com.uriel.logpose.features.voice

data class VoiceState(
    val initialized: Boolean = false,
    val listening: Boolean = false,
    val lastCommand: String? = null,
    val error: String? = null
)