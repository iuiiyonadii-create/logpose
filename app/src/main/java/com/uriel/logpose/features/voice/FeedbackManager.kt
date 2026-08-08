package com.uriel.logpose.features.voice

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.app.LogPoseApplication
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.util.*
import java.util.concurrent.PriorityBlockingQueue

enum class FeedbackPriority(val level: Int) {
    CRITICAL(1),
    NAVIGATION(2),
    CALLS(3),
    WEATHER(4),
    SYSTEM(5)
}

data class FeedbackEvent(
    val text: String,
    val priority: FeedbackPriority = FeedbackPriority.SYSTEM,
    val onComplete: () -> Unit = {},
    val timestamp: Long = System.currentTimeMillis()
) : Comparable<FeedbackEvent> {
    override fun compareTo(other: FeedbackEvent): Int {
        return this.priority.level.compareTo(other.priority.level)
    }
}

object FeedbackManager : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isReady = false
    private val eventQueue = PriorityBlockingQueue<FeedbackEvent>()
    private var isSpeaking = false
    private var currentCallback: (() -> Unit)? = null
    
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun initialize(context: Context) {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale("es", "AR")
            tts?.setPitch(1.1f)
            tts?.setSpeechRate(1.05f)
            
            isReady = true
            setupProgressListener()
            processNextEvent()
        }
    }

    fun updateVoiceSettings(pitch: Float, rate: Float) {
        tts?.setPitch(pitch)
        tts?.setSpeechRate(rate)
        LogPoseLogger.i("FeedbackManager: Voz actualizada (Pitch: $pitch, Rate: $rate)")
    }

    private fun setupProgressListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) { isSpeaking = true }
            override fun onDone(utteranceId: String?) {
                isSpeaking = false
                val callback = currentCallback
                currentCallback = null
                callback?.invoke()
                processNextEvent()
            }
            override fun onError(utteranceId: String?) {
                isSpeaking = false
                currentCallback = null
                processNextEvent()
            }
        })
    }

    fun speak(text: String, priority: FeedbackPriority = FeedbackPriority.SYSTEM, onComplete: () -> Unit = {}) {
        LogPoseLogger.d("FeedbackManager: Encolando -> $text (Prioridad: $priority)")
        eventQueue.add(FeedbackEvent(text, priority, onComplete))
        if (!isSpeaking) processNextEvent()
    }

    private fun processNextEvent() {
        if (!isReady || isSpeaking) return
        
        val event = eventQueue.poll() ?: return
        isSpeaking = true
        currentCallback = event.onComplete
        
        // Misión #008: Audio Prewarm
        // Si el SCO está activo pero no conectado físicamente, esperamos un poco.
        scope.launch {
            val commManager = LogPoseApplication.entryPoint.bluetoothCommunicationManager()
            if (commManager.isScoActive.value) {
                LogPoseLogger.d("FeedbackManager: Detectado ruteo SCO activo. Verificando enlace físico...")
                try {
                    withTimeout(2500) {
                        commManager.isScoPhysicallyConnected.first { it }
                    }
                    LogPoseLogger.d("FeedbackManager: Enlace SCO listo. Emitiendo audio.")
                } catch (e: Exception) {
                    LogPoseLogger.w("FeedbackManager: Timeout esperando SCO (Handshake lento). Emitiendo de todas formas.")
                }
            }

            // Misión #014: Blindaje de privacidad antes de hablar
            com.uriel.logpose.core.services.AudioPathGuardian.checkAndEnforceRouting()

            val params = Bundle()
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "logpose_feedback")
            tts?.speak(event.text, TextToSpeech.QUEUE_FLUSH, params, "logpose_feedback")
        }
    }

    fun stop() {
        tts?.stop()
        eventQueue.clear()
        isSpeaking = false
        currentCallback = null
    }
}
