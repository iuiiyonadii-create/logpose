package com.uriel.logpose.thamis

import android.content.Context
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.services.RecognizedVoiceCommand
import com.uriel.logpose.core.services.VoiceEngineDelegator
import com.uriel.logpose.thamis.cognitive.CognitivePipeline
import com.uriel.logpose.thamis.voice.filter.VoiceActivationGate
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * ThamisAssistant v82.0: CASCADA ESTRICTA STAFF.
 * Implementación de compuerta de activación inteligente (VoiceActivationGate).
 */
class ThamisAssistant private constructor() {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val activationGate = VoiceActivationGate()

    private val _lastResult = MutableSharedFlow<RecognizedVoiceCommand>()
    val lastResult: SharedFlow<RecognizedVoiceCommand> = _lastResult.asSharedFlow()

    companion object {
        @Volatile private var INSTANCE: ThamisAssistant? = null
        fun getInstance(context: Context): ThamisAssistant {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ThamisAssistant().also { INSTANCE = it }
            }
        }

        fun start(context: Context): ThamisAssistant {
            val instance = getInstance(context)
            VoiceEngineDelegator.start()
            return instance
        }

        fun stop() {
            VoiceEngineDelegator.stop()
        }
    }

    init {
        listenToEngine()
    }

    private fun listenToEngine() {
        scope.launch {
            VoiceEngineDelegator.observeCommands().collect { recognized ->
                if (recognized.text.isNotBlank()) {
                    processText(recognized)
                }
            }
        }
    }

    @Volatile private var isHandoverActive = false

    /**
     * v77.0 STAFF: Implementación de CASCADA ESTRICTA.
     * Nivel 1 (Vosk) detecta un disparador y despierta al Nivel 2 (Sherpa/Whisper).
     */
    private fun processText(command: RecognizedVoiceCommand) {
        if (isHandoverActive) return // Evitar ráfagas solapadas

        val noise = VoiceEngineDelegator.getAmbientNoiseLevel()

        // v82.0: Filtro de activación (VoiceActivationGate)
        val shouldProcess = activationGate.shouldProcess(
            text = command.text,
            confidence = command.confidence,
            noiseLevel = noise,
            audioDurationMs = command.durationMs
        )
        if (!shouldProcess) return
        
        // v77.0: Si Vosk detectó algo con la gramática mínima, es un disparador potencial.
        LogPoseLogger.i("ThamisAssistant", "🚀 DISPARADOR DETECTADO: '${command.text}' (Handover -> Nivel 2)")
        
        isHandoverActive = true
        
        scope.launch(Dispatchers.Main) {
            _lastResult.emit(command)
        }

        // v77.0 CASCADA: Capturamos una ráfaga de audio (5s) y la procesamos con los motores de alta precisión.
        val audioBuffer = VoiceEngineDelegator.getRecentAudioBuffer(seconds = 5)
        
        scope.launch {
            try {
                CognitivePipeline.processWithAudio(audioBuffer, command.text, noise)
            } finally {
                isHandoverActive = false
                LogPoseLogger.d("ThamisAssistant", "🏁 Handover finalizado. Sentinela de nuevo en guardia.")
            }
        }
    }
}
