package com.uriel.logpose.features.voice

import android.content.Context
import com.uriel.logpose.core.app.AppContainer
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.services.IntercomCaptureManager
import com.uriel.logpose.core.utils.AudioUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import org.json.JSONObject
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.StorageService
import java.io.File

/**
 * VoskVoiceEngine v4.8: Con Supresión de Eco por Estado de Reproducción y TTS.
 */
class VoskVoiceEngine(private var context: Context) {

    private var model: Model? = null
    @Volatile private var grammarRecognizer: Recognizer? = null
    private val recognizerLock = Any()
    
    private val filter = AudioUtils.VoiceBandPassFilter()
    private val vad = AudioUtils.EnergyVad()
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val musicController = VoiceMusicController(context)
    private val slotInterceptor = SlotInterceptor(context)

    private val _recognizedCommands = MutableSharedFlow<RecognizedCommand>()
    val recognizedCommands: SharedFlow<RecognizedCommand> = _recognizedCommands

    // SINCRO CLAUDE: El buffer alimenta al nuevo controlador de música
    private val integrityBuffer = CommandIntegrityBuffer(scope) { command, duration ->
        val wasSlot = slotInterceptor.intercept(command)
        if (!wasSlot) {
            val outcome = musicController.handleTranscript(command)
            if (outcome is VoiceMusicController.Outcome.NotUnderstood) {
                LogPoseLogger.d("Vosk: No se detectó música en '$command'")
            }
        }
        
        // Notificamos a la UI para feedback visual
        scope.launch {
            _recognizedCommands.emit(RecognizedCommand(command, 1.0f, duration))
        }
    }

    private val audioChannel = Channel<AudioChunk>(128)
    private var processingJob: Job? = null

    private var isInitialized = false
    @Volatile private var isProcessing = false
    private var silenceCounter = 0
    private var speechStartTime = 0L

    // SINCRO CLAUDE: Modo ahorro para batería baja
    @Volatile private var isPowerSaveMode = false

    data class RecognizedCommand(val text: String, val confidence: Float, val durationMs: Long = 0)
    private data class AudioChunk(val buffer: ShortArray, val length: Int)

    init {
        loadModelAsync()
        startProcessingLoop()
    }

    fun setPowerSaveMode(enabled: Boolean) {
        if (isPowerSaveMode == enabled) return
        isPowerSaveMode = enabled
        LogPoseLogger.i("ThamisBattery: VoskVoiceEngine -> Modo ahorro: $enabled")
    }

    private fun loadModelAsync() {
        // SINCRO: Desempaquetamos el modelo desde assets a la memoria interna (v8.6)
        StorageService.unpack(context, "model-es", "model",
            { m: Model ->
                model = m
                val grammarJson = VoskGrammarBuilder.buildFullGrammar()
                synchronized(recognizerLock) {
                    grammarRecognizer = Recognizer(model, 16000f, grammarJson)
                    isInitialized = true
                }
                LogPoseLogger.i("Vosk: MODELO DESEMPAQUETADO Y ACTIVO.")
            },
            { e: Exception ->
                LogPoseLogger.e("Vosk Init Error: ${e.message}. Asegurate de que 'model-es' esté en assets.")
            }
        )
    }

    private fun startProcessingLoop() {
        processingJob?.cancel()
        processingJob = scope.launch {
            for (chunk in audioChannel) {
                // SINCRO CLAUDE: Si el "gate" está cerrado (por música o por habla), ignoramos el audio
                if (!isProcessing || !AppContainer.micGate.isGateOpen()) continue
                
                synchronized(recognizerLock) {
                    val rec = grammarRecognizer ?: return@synchronized
                    try {
                        val ready = rec.acceptWaveForm(chunk.buffer, chunk.length)
                        if (ready) {
                            val text = extractText(rec.result, "text")
                            if (text.isNotBlank()) {
                                // SINCRO CLAUDE: Solo los resultados FINALES disparan la lógica pesada
                                integrityBuffer.feed(text, isFinal = true, startTime = speechStartTime)
                                speechStartTime = 0 // Reseteamos tras alimentar al buffer
                            }
                        } else {
                            // SINCRO CLAUDE: En modo ahorro de energía, omitimos procesar el JSON de parciales
                            // para reducir la carga de CPU en cada frame de audio.
                            if (isPowerSaveMode) return@synchronized

                            val partial = extractText(rec.partialResult, "partial")
                            if (partial.isNotBlank() && speechStartTime == 0L) {
                                speechStartTime = System.currentTimeMillis()
                            }
                            // Omitimos mandar parciales al buffer de integridad para evitar disparos prematuros
                        }
                    } catch (e: Exception) {
                        LogPoseLogger.e("Vosk JNI Error: ${e.message}")
                    }
                }
            }
        }
    }

    fun start() { isProcessing = true; resetSession(); attachToCapture() }
    
    fun stop() { 
        isProcessing = false 
        vad.reset()
        // SINCRO: Cerramos el hardware de audio para evitar fugas y bloqueos de sistema
        IntercomCaptureManager.stop()
        LogPoseLogger.d("Vosk: Hardware de audio liberado.")
    }
    
    fun setAttributionContext(newContext: Context) {
        this.context = newContext
    }

    // El método setMute ahora es redundante ya que el gate maneja los estados
    @Deprecated("Usar AppContainer.micGate")
    fun setMute(mute: Boolean) {
        if (mute) AppContainer.micGate.onTtsStarted() else AppContainer.micGate.onTtsEnded()
    }

    private fun attachToCapture() {
        IntercomCaptureManager.start(context) { buffer, length ->
            if (!isProcessing) return@start 
            val cleanBuffer = filter.apply(buffer, length)
            
            // Adaptación dinámica del VAD según ruido ambiente (Velocidad/Viento)
            val noiseLevel = vad.getNormalizedNoiseLevel()
            currentNoiseLevel = noiseLevel
            
            val hasVoice = vad.hasVoice(cleanBuffer, length)
            
            if (!hasVoice) silenceCounter++ else silenceCounter = 0
            
            // Si hay mucho ruido (viento > 0.7), incrementamos la exigencia del buffer
            val persistenceThreshold = if (noiseLevel > 0.7f) 40 else 30
            
            if (hasVoice || (silenceCounter in 1..persistenceThreshold)) {
                audioChannel.trySend(AudioChunk(cleanBuffer.copyOf(), length))
            }
        }
    }

    private fun resetSession() {
        synchronized(recognizerLock) { grammarRecognizer?.reset() }
        silenceCounter = 0 
    }

    private fun extractText(json: String, key: String): String = try { 
        JSONObject(json).optString(key, "") 
    } catch (e: Exception) { "" }

    companion object {
        private var currentNoiseLevel = 0.3f
        fun getAmbientNoiseLevel(): Float = currentNoiseLevel
    }
}
