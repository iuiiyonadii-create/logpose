package com.uriel.logpose.core.speech

import android.content.Context
import android.content.res.AssetManager
import com.uriel.logpose.core.compat.core.LogPoseLogger
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

import com.thamis.lab.core.common.result.LabResult
import com.thamis.lab.core.common.speech.SpeechEngine
import com.thamis.lab.core.common.speech.SpeechResult

/**
 * SherpaSpeechEngine v1.6: Reconocimiento de voz local de vocabulario abierto.
 * Mejorado v71.4: MÁXIMA ESTABILIDAD JNI. Eliminamos Hotwords para evitar crashes en Xiaomi.
 */
class SherpaSpeechEngine(private val context: Context) : SpeechEngine {

    override val engineName: String = "Sherpa-ONNX"
    
    override suspend fun transcribe(pcmData: ShortArray): LabResult<SpeechResult> {
        val t0 = System.currentTimeMillis()
        val text = transcribeShortArray(pcmData)
        return LabResult.Success(SpeechResult(
            text = text,
            confidence = 0.92f,
            latencyMs = System.currentTimeMillis() - t0,
            engineName = engineName
        ))
    }

    private var recognizer: OnlineRecognizer? = null
    private var stream: OnlineStream? = null
    private val isReady = CompletableDeferred<Boolean>()
    private val engineMutex = Mutex()

    suspend fun initEngine(): Boolean = withContext(Dispatchers.IO) {
        if (isReady.isCompleted) return@withContext isReady.await()
        
        try {
            val assetManager: AssetManager = context.assets
            val modelDir = "sherpa-onnx-es"

            val assetList = assetManager.list(modelDir) ?: emptyArray()
            val required = listOf("encoder.onnx", "decoder.onnx", "joiner.onnx", "tokens.txt")
            
            if (!required.all { it in assetList }) {
                LogPoseLogger.e("SherpaEngine", "❌ ERROR CRÍTICO: Faltan archivos en assets/$modelDir.")
                isReady.complete(false)
                return@withContext false
            }

            // v71.5: Configuración ultra-estable (BPE modeling unit + Greedy Search)
            val config = OnlineRecognizerConfig(
                modelConfig = OnlineModelConfig(
                    transducer = OnlineTransducerModelConfig(
                        encoder = "$modelDir/encoder.onnx",
                        decoder = "$modelDir/decoder.onnx",
                        joiner = "$modelDir/joiner.onnx"
                    ),
                    tokens = "$modelDir/tokens.txt",
                    numThreads = 4, 
                    debug = false,
                    modelType = "zipformer2",
                    modelingUnit = "bpe"
                ),
                decodingMethod = "greedy_search", 
                maxActivePaths = 4, 
                enableEndpoint = true
            )

            recognizer = OnlineRecognizer(assetManager, config)
            stream = recognizer?.createStream()
            LogPoseLogger.i("SherpaEngine", "✅ Motor Sherpa-ONNX Estabilizado (v71.4).")
            isReady.complete(value = true)
            true
        } catch (e: Exception) {
            LogPoseLogger.e("SherpaEngine", "❌ Error al inicializar Sherpa-ONNX: ${e.message}")
            isReady.complete(value = false)
            false
        }
    }

    suspend fun transcribe(samples: FloatArray): String = engineMutex.withLock {
        if (!isReady.await()) return ""

        val currentRecognizer = recognizer ?: return ""
        val currentStream = stream ?: return ""

        return@withLock withContext(Dispatchers.Default) {
            currentRecognizer.reset(currentStream)
            
            val chunkSize = 4000 
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

    suspend fun transcribeShortArray(pcm: ShortArray): String {
        val floatSamples = FloatArray(pcm.size) { i -> pcm[i] / 32768.0f }
        return transcribe(floatSamples)
    }

    suspend fun resetStream() = engineMutex.withLock {
        try {
            val currentRecognizer = recognizer ?: return@withLock
            val currentStream = stream ?: return@withLock
            currentRecognizer.reset(currentStream)
            currentStream.release()
            stream = currentRecognizer.createStream()
        } catch (e: Exception) {
            LogPoseLogger.e("SherpaEngine", "Error al resetear stream: ${e.message}")
        }
    }

    fun release() {
        stream?.release()
        recognizer?.release()
        stream = null
        recognizer = null
    }
}
