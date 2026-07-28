package com.uriel.logpose.thamis_ai.voice

import android.content.Context
import com.uriel.logpose.thamis_ai.nlu.NaturalLanguageEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * High-level coordinator for voice interaction.
 */
class VoiceInteractionManager(private val context: Context) {

    private val nlu = NaturalLanguageEngine()
    private val dialogue = DialogueManager()
    private val response = ResponseEngine()

    private val _state = MutableStateFlow(ConversationState.IDLE)
    val state = _state.asStateFlow()

    fun handleInput(text: String): String {
        _state.value = ConversationState.PROCESSING
        val nluResult = nlu.process(text)
        val reply = response.generate(nluResult.intent)
        _state.value = ConversationState.RESPONDING
        return reply
    }
}
