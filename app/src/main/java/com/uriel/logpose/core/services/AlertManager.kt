package com.uriel.logpose.core.services

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.features.music.MusicManager
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import java.util.*
import kotlin.coroutines.resume

data class AlertMessage(val text: String, val priority: AlertPriority = AlertPriority.NORMAL)
enum class AlertPriority { LOW, NORMAL, HIGH, SYSTEM }

/**
 * Sector 10: Sistema de Alertas con Ducking Automático y Sincronización.
 */
object AlertManager : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private val _isReady = MutableStateFlow(false)
    private val alertQueue = Channel<AlertMessage>(Channel.UNLIMITED)
    private val queueEmpty = MutableStateFlow(true)
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var toneGenerator: ToneGenerator? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    fun initialize(context: Context) {
        if (tts != null) return
        
        // SINCRO CLAUDE: Usar contexto atribuido solo en API 31+ (Fix S8 Crash)
        val attributionContext = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                context.createAttributionContext("audio_communication")
            } catch (e: Exception) {
                context
            }
        } else {
            context
        }

        tts = TextToSpeech(attributionContext, this)
        
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_VOICE_CALL, 80)
        } catch (e: Exception) {
            LogPoseLogger.e("AlertManager: No se pudo inicializar ToneGenerator: ${e.message}")
        }
        
        // Iniciamos el procesador de la cola
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

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.setLanguage(Locale.forLanguageTag("es-AR"))
            _isReady.value = true
            LogPoseLogger.i("Sector 10: Sistema de alertas activo.")
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
        // Esperamos a que el motor TTS esté cargado si aún no lo está
        if (!_isReady.value) {
            _isReady.first { it }
        }

        // 1. Bajamos el ruido de fondo y la música, silenciamos el micro
        ComfortNoiseManager.duck()
        MusicManager.duck()
        com.uriel.logpose.core.app.AppContainer.micGate.onTtsStarted()

        LogPoseLogger.d("Sector 10: Hablando -> ${alert.text}")

        val utteranceId = "alert_${System.currentTimeMillis()}"
        
            // 2. Esperamos a que termine de hablar realmente
            val streamType = if (alert.priority == AlertPriority.SYSTEM) {
                AudioManager.STREAM_MUSIC
            } else {
                AudioManager.STREAM_VOICE_CALL
            }

            suspendCancellableCoroutine<Unit> { cont ->
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(id: String?) {}
                    override fun onDone(id: String?) { if (id == utteranceId) cont.resume(Unit) }
                    override fun onError(id: String?) { if (id == utteranceId) cont.resume(Unit) }
                })
                
                val params = android.os.Bundle().apply {
                    putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, streamType)
                }
                tts?.speak(alert.text, TextToSpeech.QUEUE_ADD, params, utteranceId)
            }

        // 3. Restauramos el ruido, la música y el micro
        delay(400)
        ComfortNoiseManager.restoreVolume()
        MusicManager.unduck()
        com.uriel.logpose.core.app.AppContainer.micGate.onTtsEnded()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        toneGenerator?.release()
        toneGenerator = null
        _isReady.value = false
    }
}
