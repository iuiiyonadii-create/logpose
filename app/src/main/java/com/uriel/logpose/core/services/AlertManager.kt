package com.uriel.logpose.core.services

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import com.uriel.logpose.core.app.LogPoseApplication
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.speech.ThamisVoiceEngine
import com.uriel.logpose.features.music.MusicManager
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first

data class AlertMessage(val text: String, val priority: AlertPriority = AlertPriority.NORMAL)
enum class AlertPriority { LOW, NORMAL, HIGH, SYSTEM }

/**
 * Sector 10: Sistema de Alertas con Voz Neuronal 'MAYA'.
 * Gestión de colas de voz cruda para entornos de alto ruido.
 */
object AlertManager {
    private var voiceEngine: ThamisVoiceEngine? = null
    private val alertQueue = Channel<AlertMessage>(Channel.UNLIMITED)
    private val queueEmpty = MutableStateFlow(true)
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var toneGenerator: ToneGenerator? = null

    fun initialize(context: Context) {
        if (voiceEngine != null) return
        
        voiceEngine = ThamisVoiceEngine.getInstance(context)
        
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_VOICE_CALL, 80)
        } catch (e: Exception) {
            LogPoseLogger.e("AlertManager", "No se pudo inicializar ToneGenerator: ${e.message}")
        }
        
        // Procesador de la cola Staff
        scope.launch {
            for (message in alertQueue) {
                queueEmpty.value = false
                speakSequentially(message)
                if (alertQueue.isEmpty) {
                    queueEmpty.value = true
                }
            }
        }
    }

    fun enqueue(message: String, priority: AlertPriority = AlertPriority.NORMAL) {
        alertQueue.trySend(AlertMessage(message, priority))
    }

    fun enqueue(alert: AlertMessage) {
        alertQueue.trySend(alert)
    }

    fun beep() {
        scope.launch {
            ComfortNoiseManager.duck()
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
            delay(200)
            ComfortNoiseManager.restoreVolume()
        }
    }

    suspend fun awaitQueueDrained() {
        queueEmpty.first { it }
    }

    private suspend fun speakSequentially(alert: AlertMessage) {
        // 1. Preparación del ambiente sónico
        ComfortNoiseManager.duck()
        MusicManager.duck()
        LogPoseApplication.entryPoint.playbackAwareMicGate().onTtsStarted()

        LogPoseLogger.d("Sector 10", "Hablando vía Maya -> ${alert.text}")

        // 2. Síntesis y reproducción suspendida
        suspendCancellableCoroutine<Unit> { cont ->
            voiceEngine?.speakOffline(alert.text) {
                if (cont.isActive) cont.resumeWith(Result.success(Unit))
            }
        }

        // 3. Restauración de ruteo y volumen
        delay(400)
        ComfortNoiseManager.restoreVolume()
        MusicManager.unduck()
        LogPoseApplication.entryPoint.playbackAwareMicGate().onTtsEnded()
    }

    fun shutdown() {
        voiceEngine?.stop()
        toneGenerator?.release()
        toneGenerator = null
    }
}
