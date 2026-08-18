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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.StorageService
import java.io.File

import com.thamis.lab.core.common.result.LabResult
import com.thamis.lab.core.common.speech.SpeechEngine
import com.thamis.lab.core.common.speech.SpeechResult
import com.thamis.lab.core.common.error.LabError

/**
 * VoskVoiceEngine v6.2: Refactor DSP Automático (High-Pass + Noise Gate).
 * Blindaje térmico y acústico para hardware Xiaomi en condiciones de calle.
 * v71.4: Gramática optimizada para capturar variantes de Wake-word y comandos.
 */
class VoskVoiceEngine(context: Context) : SpeechEngine {

    private var context: Context = context.applicationContext

    override val engineName: String = "Vosk-Centinel"

    override suspend fun transcribe(pcmData: ShortArray): LabResult<SpeechResult> {
        val m = model ?: return LabResult.Failure(LabError.SystemError("Modelo no cargado"))
        return withContext(Dispatchers.IO) {
            try {
                val rec = Recognizer(m, 16000f)
                rec.acceptWaveForm(pcmData, pcmData.size)
                val resultJson = rec.result
                val text = extractTextFast(resultJson, "text")
                LabResult.Success(SpeechResult(text, 1.0f, 0, engineName))
            } catch (e: Exception) {
                LabResult.Failure(LabError.SystemError("Fallo en transcripción: ${e.message}"))
            }
        }
    }

    private var model: Model? = null
    @Volatile private var grammarRecognizer: Recognizer? = null
    private val recognizerMutex = Mutex()
    
    private val vad = AudioUtils.EnergyVad()
    
    private var lastRaw = 0f
    private var lastFiltered = 0f
    private val hpfAlpha = 0.96f // v71.7: fc=100Hz @ 16000Hz (Preservamos más cuerpo de voz)
    private var noiseGateThreshold = 25f // v71.7: Más sensible para captar finales suaves

    fun setSensitivity(level: Float) {
        val targetThreshold = (60f - (level * 50f)).coerceIn(10f, 60f)
        noiseGateThreshold = targetThreshold
        LogPoseLogger.i("Vosk", "Sensibilidad v71.7: $level (Umbral: $targetThreshold)")
    }
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _recognizedCommands = MutableSharedFlow<RecognizedCommand>()
    val recognizedCommands: SharedFlow<RecognizedCommand> = _recognizedCommands

    private val integrityBuffer = CommandIntegrityBuffer(scope) { command, duration ->
        LogPoseLogger.d("Vosk: Centinela detectó -> '$command'")
        scope.launch {
            _recognizedCommands.emit(RecognizedCommand(command, 1.0f, duration))
        }
    }

    private val audioChannel = Channel<AudioChunk>(48)
    private var processingJob: Job? = null
    
    private val chunkPool = java.util.concurrent.LinkedBlockingQueue<AudioChunk>(100)
    private fun obtainChunk(buffer: ShortArray, length: Int): AudioChunk {
        val chunk = chunkPool.poll() ?: AudioChunk(buffer, length)
        chunk.buffer = buffer
        chunk.length = length
        return chunk
    }
    private fun releaseChunk(chunk: AudioChunk) {
        chunkPool.offer(chunk)
    }

    private val rollingBuffer = ShortArray(16000 * 10)
    private var writePointer = 0
    private val bufferLock = Any()

    @Volatile private var isInitialized = false
    @Volatile private var isModelLoading = false
    @Volatile private var isProcessing = false
    private var silenceCounter = 0
    private var speechStartTime = 0L
    private var lastPartialText = ""
    private var lastPartialRequestTime = 0L

    @Volatile private var isPowerSaveMode = false

    data class RecognizedCommand(val text: String, val confidence: Float, val durationMs: Long = 0)
    private class AudioChunk(var buffer: ShortArray, var length: Int)

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
        if (isInitialized && (model != null)) return
        if (isModelLoading) return
        isModelLoading = true

        scope.launch(Dispatchers.IO) {
            try {
                val modelFolder = File(context.getExternalFilesDir(null), "model/model-es")
                val dictionaryFile = File(modelFolder, "graph/words.txt")
                
                if (dictionaryFile.exists()) {
                    val content = dictionaryFile.readText()
                    if (!content.contains("log 425115")) {
                        LogPoseLogger.w("Vosk", "🧠 Cerebro acústico desactualizado. Forzando Hard Reset...")
                        modelFolder.deleteRecursively()
                    }
                }

                if (modelFolder.exists() && modelFolder.isDirectory && (modelFolder.list()?.isNotEmpty() == true)) {
                    LogPoseLogger.i("Vosk", "Intentando cargar modelo desde caché local...")
                    try {
                        model = Model(modelFolder.absolutePath)
                        updateGrammar()
                        isModelLoading = false
                        return@launch
                    } catch (_: Exception) {
                        LogPoseLogger.w("Vosk", "Modelo corrupto en caché. Eliminando para re-unpack.")
                        modelFolder.deleteRecursively()
                    }
                }

                StorageService.unpack(context, "model-es", "model",
                    { m: Model ->
                        model = m
                        updateGrammar()
                        isModelLoading = false
                    },
                    { e: Exception ->
                        LogPoseLogger.e("Vosk", "Error crítico en unpack: ${e.message}")
                        isModelLoading = false
                    }
                )
            } catch (e: Exception) {
                LogPoseLogger.e("Vosk", "Fallo general en carga de modelo: ${e.message}")
                isModelLoading = false
            }
        }
    }

    fun updateGrammar() {
        val m = model ?: return
        scope.launch(Dispatchers.IO) {
            LogPoseLogger.i("LogPose", "Vosk: Activando modo SENTINELA (v77.0) - Cascada Estricta.")
            try {
                // v77.0 STAFF: Restauramos la gramática mínima. 
                // Vosk solo debe detectar Wake-words o Verbos clave para despertar a los motores pesados.
                val grammar = VoskGrammarBuilder.buildMinimalGrammar()
                val newRecognizer = Recognizer(m, 16000f, grammar) 
                
                recognizerMutex.withLock {
                    val oldRecognizer = grammarRecognizer
                    grammarRecognizer = newRecognizer
                    oldRecognizer?.close()
                    isInitialized = true
                }
                LogPoseLogger.d("Vosk: Sentinela activo. Esperando disparadores...")
            } catch (e: Exception) {
                LogPoseLogger.e("Vosk", "Error al inicializar Sentinela: ${e.message}")
            }
        }
    }

    private fun startProcessingLoop() {
        processingJob?.cancel()
        processingJob = scope.launch {
            for (chunk in audioChannel) {
                try {
                    if (!isProcessing) continue
                    
                    if (!LogPoseApplication.entryPoint.playbackAwareMicGate().isGateOpen()) {
                        continue
                    }
                    
                    synchronized(bufferLock) {
                        val remainingSpace = rollingBuffer.size - writePointer
                        writePointer = if (chunk.length <= remainingSpace) {
                            System.arraycopy(chunk.buffer, 0, rollingBuffer, writePointer, chunk.length)
                            (writePointer + chunk.length) % rollingBuffer.size
                        } else {
                            System.arraycopy(chunk.buffer, 0, rollingBuffer, writePointer, remainingSpace)
                            val leftover = chunk.length - remainingSpace
                            System.arraycopy(chunk.buffer, remainingSpace, rollingBuffer, 0, leftover)
                            leftover
                        }
                    }

                    recognizerMutex.withLock {
                        val rec = grammarRecognizer
                        if (rec != null) {
                            val ready = rec.acceptWaveForm(chunk.buffer, chunk.length)
                            if (ready) {
                                val resultJson = rec.result
                                val text = extractTextFast(resultJson, "text")
                                
                                if (text.isNotBlank()) {
                                    LogPoseLogger.i("Vosk", "🧠 Cerebro Acústico detectó: '$text'")
                                    lastPartialText = ""
                                    integrityBuffer.feed(text, isFinal = true, startTime = speechStartTime)
                                    speechStartTime = 0 
                                }
                            } else {
                                if (!isPowerSaveMode) {
                                    val now = System.currentTimeMillis()
                                    if (now - lastPartialRequestTime >= 1000) {
                                        lastPartialRequestTime = now
                                        val partialJson = rec.partialResult
                                        val partial = extractTextFast(partialJson, "partial")
                                        if (partial.isNotBlank() && partial != lastPartialText) {
                                            lastPartialText = partial
                                            if (speechStartTime == 0L) {
                                                speechStartTime = System.currentTimeMillis()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    LogPoseLogger.e("Vosk JNI Error: ${e.message}")
                } finally {
                    IntercomCaptureManager.releaseBuffer(chunk.buffer)
                    releaseChunk(chunk)
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
        this.context = newContext.applicationContext
    }

    private fun attachToCapture() {
        IntercomCaptureManager.start(context) { buffer, length ->
            if (!isProcessing) {
                IntercomCaptureManager.releaseBuffer(buffer)
                return@start
            }

            if (System.currentTimeMillis() % 5000 < 50) { 
                 LogPoseLogger.d("Vosk", "Mic Heartbeat: Ruido $currentNoiseLevel | ASR Activo")
            }

            for (i in 0 until length) {
                val current = buffer[i].toInt()
                val filtered = (hpfAlpha * (lastFiltered + current - lastRaw)).toInt()
                lastRaw = current.toFloat()
                lastFiltered = filtered.toFloat()
                buffer[i] = filtered.coerceIn(-32768, 32767).toShort()
            }

            val noiseLevel = vad.getNormalizedNoiseLevel()
            currentNoiseLevel = noiseLevel
            
            // v71.7: VAD Pro-Rider Suave - Umbral reducido para no cortar payloads (canciones/nombres)
            val dynamicMultiplier = if (noiseLevel > 0.6f) 1.15f else 1.0f
            val hasVoice = vad.hasVoice(buffer, length, multiplier = dynamicMultiplier)
            
            silenceCounter = if (!hasVoice) silenceCounter + 1 else 0
            // v71.7: Inercia Rider - Mayor persistencia para capturar pausas naturales
            val persistenceThreshold = if (noiseLevel > 0.7f) 60 else 45
            
            if (hasVoice || (silenceCounter in 1..persistenceThreshold)) {
                val chunk = obtainChunk(buffer, length)
                val sent = audioChannel.trySend(chunk).isSuccess
                if (!sent) {
                    IntercomCaptureManager.releaseBuffer(buffer)
                    releaseChunk(chunk)
                }
            } else {
                IntercomCaptureManager.releaseBuffer(buffer)
            }
        }
    }

    private fun resetSession() {
        scope.launch(Dispatchers.IO) {
            if (recognizerMutex.tryLock()) {
                try {
                    grammarRecognizer?.reset() 
                } finally {
                    recognizerMutex.unlock()
                }
            }
        }
        silenceCounter = 0 
    }

    private fun extractTextFast(json: String, key: String): String {
        val searchKey = "\"$key\""
        val keyIndex = json.indexOf(searchKey)
        if (keyIndex == -1) return ""
        
        val colonIndex = json.indexOf(":", keyIndex + searchKey.length)
        if (colonIndex == -1) return ""
        
        val startQuote = json.indexOf("\"", colonIndex)
        if (startQuote == -1) return ""
        
        val endQuote = json.indexOf("\"", startQuote + 1)
        if (endQuote == -1) return ""
        
        return json.substring(startQuote + 1, endQuote)
    }

    fun getRecentAudioBuffer(seconds: Int = 4): ShortArray {
        synchronized(bufferLock) {
            val samplesNeeded = (16000 * seconds).coerceAtMost(rollingBuffer.size)
            val result = ShortArray(samplesNeeded)
            
            var readPointer = (writePointer - samplesNeeded)
            if (readPointer < 0) readPointer += rollingBuffer.size
            
            val part1 = rollingBuffer.size - readPointer
            if (part1 >= samplesNeeded) {
                System.arraycopy(rollingBuffer, readPointer, result, 0, samplesNeeded)
            } else {
                System.arraycopy(rollingBuffer, readPointer, result, 0, part1)
                val part2 = samplesNeeded - part1
                System.arraycopy(rollingBuffer, 0, result, part1, part2)
            }
            return result
        }
    }

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
