package com.uriel.logpose.features.voice

import android.content.Context
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.app.LogPoseApplication
import com.uriel.logpose.core.speech.ThamisVoiceEngine
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
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

/**
 * FeedbackManager v2.0: Orquestador Sónico con Motor Neuronal 'MAYA'.
 * Gestión de colas por prioridad y sincronización SCO.
 */
object FeedbackManager {
    private var voiceEngine: ThamisVoiceEngine? = null
    private val eventQueue = PriorityBlockingQueue<FeedbackEvent>()
    private var isSpeaking = false
    
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun initialize(context: Context) {
        voiceEngine = ThamisVoiceEngine.getInstance(context)
    }

    fun speak(text: String, priority: FeedbackPriority = FeedbackPriority.SYSTEM, onComplete: () -> Unit = {}) {
        LogPoseLogger.d("FeedbackManager", "Encolando -> $text (Prioridad: $priority)")
        eventQueue.add(FeedbackEvent(text, priority, onComplete))
        if (!isSpeaking) processNextEvent()
    }

    private fun processNextEvent() {
        if (isSpeaking) return
        
        val event = eventQueue.poll() ?: return
        isSpeaking = true
        
        scope.launch {
            // v56.0: Handshake SCO Inteligente - Reducimos el timeout a 800ms
            // Si ya está activo, no esperamos nada.
            val commManager = LogPoseApplication.entryPoint.bluetoothCommunicationManager()
            if (commManager.isScoActive.value && !commManager.isScoPhysicallyConnected.value) {
                try {
                    withTimeout(800) {
                        commManager.isScoPhysicallyConnected.first { it }
                    }
                } catch (e: Exception) {
                    // Fallback rápido
                }
            }

            // Misión #014: Blindaje de privacidad antes de hablar
            com.uriel.logpose.core.services.AudioPathGuardian.checkAndEnforceRouting()

            LogPoseLogger.i("FeedbackManager", "Emitiendo vía Maya: ${event.text}")
            
            voiceEngine?.speakOffline(event.text) {
                event.onComplete()
                isSpeaking = false
                processNextEvent()
            }
        }
    }

    fun stop() {
        voiceEngine?.stop()
        eventQueue.clear()
        isSpeaking = false
    }
}
