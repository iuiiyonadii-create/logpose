package com.uriel.logpose.features.voice

import android.util.Log
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.parser.EntitySanitizer
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicReference

/**
 * CommandIntegrityBuffer V3: Maneja ráfagas de viento y comandos cortados.
 */
class CommandIntegrityBuffer(
    private val scope: CoroutineScope,
    private val onCompleteCommand: (String, Long) -> Unit
) {
    enum class State { IDLE, COLLECTING, ANCHOR_WAITING }

    private val state = AtomicReference(State.IDLE)
    private val accumulatedText = StringBuilder()
    private var bufferJob: Job? = null
    private var firstStartTime = 0L

    private val ANCHOR_KEYWORDS = setOf("pone", "pon", "play", "reproduce", "reproduci", "abrir", "abri", "abre")

    fun feed(text: String, isFinal: Boolean, startTime: Long = 0L) {
        if (text.isBlank()) return
        
        if (state.get() == State.IDLE) {
            accumulatedText.clear()
            accumulatedText.append(text)
            state.set(State.COLLECTING)
            firstStartTime = if (startTime > 0) startTime else System.currentTimeMillis()
        } else {
            accumulatedText.append(" ").append(text)
        }

        if (isFinal) {
            val current = accumulatedText.toString()
            if (isAnchorOnly(current)) {
                state.set(State.ANCHOR_WAITING)
                startTimer(2000L) // Espera agresiva
            } else {
                finalizeBuffer()
            }
        } else {
            startTimer(800L) // Parcial normal
        }
    }

    private fun startTimer(ms: Long) {
        bufferJob?.cancel()
        bufferJob = scope.launch {
            delay(ms)
            finalizeBuffer()
        }
    }

    private fun finalizeBuffer() {
        val command = accumulatedText.toString().trim()
        val duration = if (firstStartTime > 0) System.currentTimeMillis() - firstStartTime else 0L

        if (command.length >= 3 && !isAnchorOnly(command)) {
            onCompleteCommand(command, duration)
        }
        state.set(State.IDLE)
        accumulatedText.clear()
        firstStartTime = 0
    }

    private fun isAnchorOnly(text: String): Boolean {
        val normalized = MusicVocabulary.normalize(text)
        val hasAnchor = ANCHOR_KEYWORDS.any { normalized.contains(it) }
        val sanitized = EntitySanitizer.sanitize(text)
        return hasAnchor && sanitized.isBlank()
    }
}
