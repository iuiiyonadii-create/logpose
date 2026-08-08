package com.uriel.logpose.core.speech

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import com.k2fsa.sherpa.onnx.OnlineModelConfig
import com.k2fsa.sherpa.onnx.OnlineRecognizer
import com.k2fsa.sherpa.onnx.OnlineRecognizerConfig
import com.k2fsa.sherpa.onnx.OnlineStream
import com.k2fsa.sherpa.onnx.OnlineTransducerModelConfig

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * SherpaSpeechEngine v1.2: Reconocimiento de voz local de vocabulario abierto.
 * Mejorado: Thread-safe con Mutex para evitar SIGSEGV en JNI.
 */
class SherpaSpeechEngine(private val context: Context) {

    private var recognizer: OnlineRecognizer? = null
    private var stream: OnlineStream? = null
    private val isReady = CompletableDeferred<Boolean>()
    private val engineMutex = Mutex()

    suspend fun initEngine(): Boolean = withContext(Dispatchers.IO) {
        if (isReady.isCompleted) return@withContext isReady.await()
        
        try {
            val assetManager: AssetManager = context.assets
            val modelDir = "sherpa-onnx-es"
            
            // Verificación Staff: Validar existencia de archivos críticos
            val requiredFiles = listOf("encoder.onnx", "tokens.txt")
            val existingFiles = assetManager.list(modelDir) ?: emptyArray()
            
            if (!requiredFiles.all { it in existingFiles }) {
                Log.e("SherpaEngine", "❌ Faltan archivos del modelo en assets/$modelDir. Abortando.")
                isReady.complete(value = false)
                return@withContext false
            }

            val config = OnlineRecognizerConfig(
                modelConfig = OnlineModelConfig(
                    transducer = OnlineTransducerModelConfig(
                        encoder = "$modelDir/encoder.onnx",
                        decoder = "$modelDir/decoder.onnx",
                        joiner = "$modelDir/joiner.onnx"
                    ),
                    tokens = "$modelDir/tokens.txt",
                    numThreads = 2, 
                    debug = false,
                    modelType = "zipformer2" 
                ),
                decodingMethod = "greedy_search", // v9.0: Búsqueda rápida Staff
                enableEndpoint = true 
            )

            recognizer = OnlineRecognizer(assetManager, config)
            stream = recognizer?.createStream()
            Log.i("SherpaEngine", "✅ Motor Sherpa-ONNX listo (Modelo Zipformer2 Staff).")
            isReady.complete(value = true)
            true
        } catch (e: Exception) {
            Log.e("SherpaEngine", "❌ Error al inicializar Sherpa-ONNX: ${e.message}")
            isReady.complete(value = false)
            false
        }
    }

    /**
     * Procesa muestras de audio FloatArray (-1.0f a 1.0f) a 16000 Hz.
     * v7.4: Mutex lock para prevenir colisiones en JNI.
     */
    suspend fun transcribe(samples: FloatArray): String = engineMutex.withLock {
        if (!isReady.await()) return ""

        val currentRecognizer = recognizer ?: return ""
        val currentStream = stream ?: return ""

        return@withLock withContext(Dispatchers.Default) {
            currentRecognizer.reset(currentStream)
            
            // v8.0: Inferencia Hyper-Rápida por Chunks
            val chunkSize = 4000 // 250ms de audio por ciclo
            var offset = 0
            while (offset < samples.size) {
                val end = minOf(offset + chunkSize, samples.size)
                val chunk = samples.sliceArray(offset until end)
                currentStream.acceptWaveform(chunk, sampleRate = 16000)
                
                while (currentRecognizer.isReady(currentStream)) {
                    currentRecognizer.decode(currentStream)
                }
                offset += chunkSize
            }

            currentRecognizer.getResult(currentStream).text.trim()
        }
    }

    /**
     * v7.4: Thread-safe reset.
     */
    suspend fun resetStream() = engineMutex.withLock {
        try {
            val currentRecognizer = recognizer ?: return@withLock
            val currentStream = stream ?: return@withLock
            currentRecognizer.reset(currentStream)
            currentStream.release()
            stream = currentRecognizer.createStream()
            Log.d("SherpaEngine", "Stream Staff re-instanciado (Zero-Echo Sync).")
        } catch (e: Exception) {
            Log.e("SherpaEngine", "Error al resetear stream: ${e.message}")
        }
    }

    fun release() {
        stream?.release()
        recognizer?.release()
        stream = null
        recognizer = null
    }
}
