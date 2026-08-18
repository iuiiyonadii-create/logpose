package com.uriel.logpose.core.speech

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Environment
import java.io.File
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.services.LogPoseHudService
import com.uriel.logpose.features.music.MusicManager
import com.k2fsa.sherpa.onnx.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * 🔊 THAMIS VOICE ENGINE v5.0 - NEURONAL SHREPA-ONNX TTS (MAYA ACTIVA)
 * ------------------------------------------------------------------
 * Refactor Maestro: Activación de síntesis neuronal local Sherpa-ONNX.
 * Eliminación total de motores Android legacy y MP3 estáticos.
 */
class ThamisVoiceEngine private constructor(context: Context) {

    private val appContext = context.applicationContext
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val engineMutex = Mutex()
    
    private var offlineTts: OfflineTts? = null
    private var isTtsReady = false

    // v61.0: PCM Buffer Pool to reduce GC pressure during synthesis
    private val pcmBufferPool = java.util.concurrent.LinkedBlockingQueue<ShortArray>(10)
    
    // v63.0: Speech Cache for instant responses
    private val speechCache = mutableMapOf<String, FloatArray>()

    companion object {
        @Volatile
        private var INSTANCE: ThamisVoiceEngine? = null

        fun getInstance(context: Context): ThamisVoiceEngine {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ThamisVoiceEngine(context).also { INSTANCE = it }
            }
        }
    }

    init {
        initializeNeuronalTts()
    }

    private fun initializeNeuronalTts() {
        scope.launch(Dispatchers.IO) {
            try {
                LogPoseLogger.i("ThamisVoiceEngine", "Buscando cuerdas vocales de Maya...")

                val modelsRoot = File(appContext.getExternalFilesDir(null), "Models/maya")
                if (!modelsRoot.exists()) {
                    modelsRoot.mkdirs()
                    LogPoseLogger.i("ThamisVoiceEngine", "📁 Carpeta de modelos creada en: ${modelsRoot.absolutePath}")
                }
                
                // v120.0 STAFF: Unificación de rutas de modelos (SD Card + Internal)
                val possibleDirs = listOf(
                    modelsRoot,
                    File("/sdcard/LogPose/Models/maya"),
                    File("/storage/emulated/0/LogPose/Models/maya"),
                    File(appContext.getExternalFilesDir(null), "sherpa-onnx-es"),
                    File("/sdcard/sherpa-onnx-es")
                )
                
                var modelDir: File? = null
                for (dir in possibleDirs) {
                    if (File(dir, "model.onnx").exists()) {
                        modelDir = dir
                        LogPoseLogger.i("ThamisVoiceEngine", "📍 Cuerdas vocales de Maya halladas en: ${dir.absolutePath}")
                        break
                    }
                }

                if (modelDir == null) {
                    LogPoseLogger.w("ThamisVoiceEngine", "Maya en standby. Modelos no encontrados en LogPose/Models/maya")
                    LogPoseHudService.updateStatus("⚪ MAYA: STANDBY")
                    return@launch
                }

                val modelFile = File(modelDir, "model.onnx")
                val tokensFile = File(modelDir, "tokens.txt")
                
                val config = OfflineTtsConfig(
                    model = OfflineTtsModelConfig(
                        vits = OfflineTtsVitsModelConfig(
                            model = modelFile.absolutePath,
                            tokens = tokensFile.absolutePath
                        ),
                        numThreads = 2,
                        debug = false
                    )
                )
                
                // AssetManager = null para carga desde File System
                offlineTts = OfflineTts(null, config)
                isTtsReady = true
                
                withContext(Dispatchers.Main) {
                    LogPoseLogger.i("ThamisVoiceEngine", "✅ MAYA: 🟢 ACTIVO")
                    LogPoseHudService.updateStatus("🟢 MAYA: ACTIVA")
                }
            } catch (e: Exception) {
                LogPoseLogger.e("ThamisVoiceEngine", "❌ Fallo al despertar a Maya: ${e.message}")
                LogPoseHudService.updateStatus("🔴 MAYA: ERROR")
            }
        }
    }

    /**
     * HABLA DINÁMICA NEURONAL: Síntesis local a 0ms de latencia (In-Memory).
     */
    fun speakOffline(text: String, onComplete: () -> Unit = {}) {
        if (text.isBlank()) {
            onComplete()
            return
        }

        scope.launch(Dispatchers.IO) {
            engineMutex.withLock {
                val tts = offlineTts
                if (tts == null || !isTtsReady) {
                    LogPoseLogger.w("ThamisVoiceEngine", "Maya en standby. Ignorando: $text")
                    onComplete()
                    return@withLock
                }

                try {
                    // 1. Ducking Automático (Sincronizado v88.0)
                    MusicManager.duck()
                    
                    LogPoseLogger.d("ThamisVoiceEngine", "🧠 Maya procesando: '$text'")
                    
                    // 2. Síntesis Neuronal Sherpa-ONNX (con Speed Modulation v63.0)
                    val cachedSamples = speechCache[text]
                    val samples: FloatArray
                    val sampleRate: Int

                    if (cachedSamples != null) {
                        LogPoseLogger.d("ThamisVoiceEngine", "🚀 Cache Hit: '$text'")
                        samples = cachedSamples
                        sampleRate = 22050 // Default for Sherpa
                    } else {
                        // v63.0: Modulación de velocidad según la marcha
                        val currentSpeed = com.uriel.logpose.thamis.world.engine.WorldModelEngine.getCurrentSnapshot().vehicle.speedKmh
                        val speedFactor = if (currentSpeed > 80) 1.2f else 1.0f
                        
                        val audio = tts.generate(text, speed = speedFactor)
                        samples = audio.samples
                        sampleRate = audio.sampleRate
                        
                        // Cacheamos frases cortas y comunes
                        if (text.length < 20) speechCache[text] = samples
                    }
                    
                    if (samples.isEmpty()) {
                        LogPoseLogger.e("ThamisVoiceEngine", "Error: Síntesis vacía.")
                        MusicManager.unduck()
                        onComplete()
                        return@withLock
                    }

                    // 3. Reproducción directa vía AudioTrack (Low Latency Stream)
                    playPcmStream(samples, sampleRate)
                    
                    // 4. Restauración de volumen
                    MusicManager.unduck()
                    onComplete()
                    
                } catch (e: Exception) {
                    LogPoseLogger.e("ThamisVoiceEngine", "Error en síntesis: ${e.message}")
                    MusicManager.unduck()
                    onComplete()
                }
            }
        }
    }

    private suspend fun playPcmStream(samples: FloatArray, sampleRate: Int) = withContext(Dispatchers.IO) {
        val minBufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build())
            .setAudioFormat(AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(sampleRate)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build())
            .setBufferSizeInBytes(minBufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM) // v118.5: Modo Stream para evitar cortes
            .build()

        // v61.0: Re-uso de buffer del pool
        val polled = pcmBufferPool.poll()
        val shortSamples = if (polled != null && polled.size >= samples.size) polled else ShortArray(samples.size)
        
        for (i in samples.indices) {
            shortSamples[i] = (samples[i] * 32767).toInt().coerceIn(-32768, 32767).toShort()
        }

        audioTrack.play()
        audioTrack.write(shortSamples, 0, samples.size)
        
        // Devolvemos al pool si no es una creación nueva excesivamente grande
        if (shortSamples.size < 500000) { // ~1MB
            pcmBufferPool.offer(shortSamples)
        }
        
        // v118.5: Espera no bloqueante usando delay de corrutina
        val durationMs = (samples.size.toFloat() / sampleRate * 1000).toLong()
        delay(durationMs + 50)
        
        audioTrack.stop()
        audioTrack.release()
    }

    fun stop() {
        // En modo neuronal, el stop es implícito al terminar el buffer,
        // pero podemos implementar una cancelación si fuera necesario.
    }

    fun release() {
        offlineTts?.release()
        isTtsReady = false
        LogPoseLogger.i("ThamisVoiceEngine", "MAYA OFFLINE: Módulo neuronal liberado.")
    }
}
