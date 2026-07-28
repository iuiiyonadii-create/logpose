package com.uriel.logpose.core.voice

import android.content.Context
import com.uriel.logpose.domain.models.LogPoseCommand
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Orchestrates STT, Command Processing, and TTS.
 */
class VoiceEngine(private val context: Context) {

    private val stt = SpeechRecognizerManager(context)
    private val tts = TextToSpeechManager(context)
    private val processor = VoiceCommandProcessor()

    private val _voiceState = MutableStateFlow(VoiceState.IDLE)
    val voiceState = _voiceState.asStateFlow()

    fun startListening() {
        _voiceState.value = VoiceState.LISTENING
        stt.startListening()
    }

    fun stopListening() {
        stt.stopListening()
        _voiceState.value = VoiceState.IDLE
    }

    fun speak(text: String) {
        _voiceState.value = VoiceState.SPEAKING
        tts.speak(text)
    }

    fun processResult(text: String): LogPoseCommand {
        _voiceState.value = VoiceState.PROCESSING
        return processor.processText(text)
    }
}
