package com.uriel.logpose.features.voice

import android.content.Context
import com.uriel.logpose.core.app.LogPoseApplication
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
 * VoskVoiceEngine v5.2: Con Actualización Dinámica de Gramática (Misión #013).
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

    private val integrityBuffer = CommandIntegrityBuffer(scope) { command, duration ->
        val wasSlot = slotInterceptor.intercept(command)
        if (!wasSlot) {
            // v4.6: Si detectamos música, pero el texto de Vosk es basura (reloj, mental),
            // el pipeline principal debería disparar el análisis del audio buffer.
            LogPoseLogger.d("Vosk: Centinela detectó -> '$command'")
        }
        
        scope.launch {
            _recognizedCommands.emit(RecognizedCommand(command, 1.0f, duration))
        }
    }

    private val audioChannel = Channel<AudioChunk>(128)
    private var processingJob: Job? = null
    
    // --- ESTRATEGIA HÍBRIDA v4.6: ROLLING BUFFER ---
    private val rollingBuffer = ShortArray(16000 * 8) // 8 segundos Staff Standard (Anti-Clipping)
    private var writePointer = 0
    private val bufferLock = Any()

    private var isInitialized = false
    @Volatile private var isProcessing = false
    private var silenceCounter = 0
    private var speechStartTime = 0L

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
        if (isInitialized && model != null) return
        
        StorageService.unpack(context, "model-es", "model",
            { m: Model ->
                model = m
                updateGrammar()
            },
            { e: Exception ->
                LogPoseLogger.e("Vosk Init Error: ${e.message}")
            }
        )
    }

    /**
     * Re-compila la gramática JSON e instancia un nuevo Recognizer sin reiniciar el modelo.
     */
    fun updateGrammar() {
        val m = model ?: return
        LogPoseLogger.i("Vosk: Actualizando gramática dinámica...")
        
        val grammarJson = VoskGrammarBuilder.buildFullGrammar()
        synchronized(recognizerLock) {
            try {
                grammarRecognizer?.close()
                grammarRecognizer = Recognizer(m, 16000f, grammarJson)
                isInitialized = true
                LogPoseLogger.d("Vosk: Gramática actualizada con éxito.")
            } catch (e: Exception) {
                LogPoseLogger.e("Vosk: Error al instanciar Recognizer con nueva gramática: ${e.message}")
            }
        }
    }

    fun releaseResources() {
        LogPoseLogger.i("Vosk: Liberando recursos pesados.")
        stop()
        synchronized(recognizerLock) {
            try {
                grammarRecognizer?.close()
            } catch (e: Exception) {}
            grammarRecognizer = null
            model = null
            isInitialized = false
        }
        System.gc()
    }

    private fun startProcessingLoop() {
        processingJob?.cancel()
        processingJob = scope.launch {
            for (chunk in audioChannel) {
                if (!isProcessing || !LogPoseApplication.entryPoint.playbackAwareMicGate().isGateOpen()) continue
                
                // Misión #022.3: Llenado de Rolling Buffer para Handover v4.6
                synchronized(bufferLock) {
                    chunk.buffer.forEach { sample ->
                        rollingBuffer[writePointer] = sample
                        writePointer = (writePointer + 1) % rollingBuffer.size
                    }
                }

                synchronized(recognizerLock) {
                    val rec = grammarRecognizer ?: return@synchronized
                    try {
                        val ready = rec.acceptWaveForm(chunk.buffer, chunk.length)
                        if (ready) {
                            val text = extractText(rec.result, "text")
                            if (text.isNotBlank()) {
                                integrityBuffer.feed(text, isFinal = true, startTime = speechStartTime)
                                speechStartTime = 0 
                            }
                        } else {
                            if (isPowerSaveMode) return@synchronized
                            val partial = extractText(rec.partialResult, "partial")
                            if (partial.isNotBlank() && speechStartTime == 0L) {
                                speechStartTime = System.currentTimeMillis()
                            }
                        }
                    } catch (e: Exception) {
                        LogPoseLogger.e("Vosk JNI Error: ${e.message}")
                    } finally {
                        // v1.7: Devolver al pool para reducir GC (Misión #029)
                        IntercomCaptureManager.releaseBuffer(chunk.buffer)
                    }
                }
            }
        }
    }

    fun start() { 
        if (!isInitialized) loadModelAsync()
        isProcessing = true
        resetSession()
        attachToCapture() 
    }
    
    fun stop() { 
        isProcessing = false 
        vad.reset()
        IntercomCaptureManager.stop()
        LogPoseLogger.d("Vosk: Hardware de audio liberado.")
    }
    
    fun setAttributionContext(newContext: Context) {
        this.context = newContext
    }

    private fun attachToCapture() {
        IntercomCaptureManager.start(context) { buffer, length ->
            if (!isProcessing) {
                IntercomCaptureManager.releaseBuffer(buffer)
                return@start
            }
            val cleanBuffer = filter.apply(buffer, length)
            val noiseLevel = vad.getNormalizedNoiseLevel()
            currentNoiseLevel = noiseLevel
            val hasVoice = vad.hasVoice(cleanBuffer, length)
            
            silenceCounter = if (!hasVoice) silenceCounter + 1 else 0
            val persistenceThreshold = if (noiseLevel > 0.7f) 40 else 30
            
            if (hasVoice || (silenceCounter in 1..persistenceThreshold)) {
                // Ya no hacemos copyOf(), usamos el buffer del pool (Misión #029)
                audioChannel.trySend(AudioChunk(buffer, length))
            } else {
                IntercomCaptureManager.releaseBuffer(buffer)
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

    /**
     * Misión #022.3: Extrae una copia instantánea del audio PCM reciente.
     * Útil para que Whisper procese nombres que Vosk ignora.
     */
    fun getRecentAudioBuffer(): ShortArray {
        synchronized(bufferLock) {
            val result = ShortArray(rollingBuffer.size)
            // Re-alineamos el buffer circular para que sea lineal
            val part1 = rollingBuffer.size - writePointer
            System.arraycopy(rollingBuffer, writePointer, result, 0, part1)
            System.arraycopy(rollingBuffer, 0, result, part1, writePointer)
            return result
        }
    }

    /**
     * Staff v5.4: Limpia el buffer de audio para evitar el "eco" de comandos previos.
     */
    fun clearRollingBuffer() {
        synchronized(bufferLock) {
            rollingBuffer.fill(0)
            writePointer = 0
            LogPoseLogger.d("Vosk: Rolling buffer purgado.")
        }
    }

    companion object {
        private var currentNoiseLevel = 0.3f
        fun getAmbientNoiseLevel(): Float = currentNoiseLevel
    }
}
