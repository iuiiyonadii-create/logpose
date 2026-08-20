package com.uriel.logpose.thamis.intelligence

import android.content.Context
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.uriel.logpose.core.compat.core.LogPoseLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File

/**
 * ThamisNeuralEngine v68.0: Consolidación de Conciencia con Modo Degradado.
 * Unifica LocalLLMEngine y EdgeBrainManager en un solo motor resiliente.
 * Expone StateFlow<Boolean> isDegraded cuando el modelo LLM no está presente.
 */
object ThamisNeuralEngine {

    private var llmInference: LlmInference? = null
    private val initMutex = Mutex()
    private val inferenceMutex = Mutex()
    private var isInitialized = false

    private val _isDegraded = MutableStateFlow(true)
    val isDegraded: StateFlow<Boolean> = _isDegraded.asStateFlow()

    fun isDegraded(): Boolean = _isDegraded.value

    /**
     * Inicialización unificada del modelo Llama 3.2 1B / Gemma.
     */
    suspend fun initialize(context: Context): Boolean = initMutex.withLock {
        if (isInitialized) {
            _isDegraded.value = false
            return true
        }

        val success = withContext(Dispatchers.IO) {
            try {
                LogPoseLogger.i("NeuralEngine", "Despertando conciencia unificada Staff...")
                
                // v67.0: Estandarización de ruta y nombre de modelo
                val modelFile = File(context.getExternalFilesDir(null), "llm/staff_brain.bin")
                if (!modelFile.exists()) {
                    LogPoseLogger.w("NeuralEngine", "Modelo no encontrado en ${modelFile.absolutePath} -> Activando MODO DEGRADADO")
                    return@withContext false
                }

                val options = LlmInference.LlmInferenceOptions.builder()
                    .setModelPath(modelFile.absolutePath)
                    .setMaxTokens(128)
                    .setTemperature(0.7f)
                    .build()

                llmInference = LlmInference.createFromOptions(context, options)
                isInitialized = true
                LogPoseLogger.i("NeuralEngine", "✅ Conciencia ONLINE (Modo Híbrido Resiliente).")
                true
            } catch (e: Exception) {
                LogPoseLogger.e("NeuralEngine", "❌ Error al despertar cerebro: ${e.message} -> Activando MODO DEGRADADO")
                false
            }
        } ?: false

        _isDegraded.value = !success
        return success
    }

    /**
     * Generación de respuesta con bloqueo de concurrencia.
     */
    suspend fun generateResponse(prompt: String): String = inferenceMutex.withLock {
        if (!isInitialized || llmInference == null) return ""
        
        return withContext(Dispatchers.Default) {
            try {
                // v66.0 Guard: Truncamiento preventivo
                val safePrompt = if (prompt.length > 800) prompt.take(800) else prompt
                val minPrompt = "Asistente Moto. Responde solo JSON.\nUser: $safePrompt\nAssistant:"
                
                LogPoseLogger.d("NeuralEngine", "Razonando: '$safePrompt'")
                llmInference?.generateResponse(minPrompt) ?: ""
            } catch (e: Exception) {
                LogPoseLogger.e("NeuralEngine", "Error Inferencia: ${e.message}")
                ""
            }
        }
    }

    fun isReady(): Boolean = isInitialized

    fun release() {
        llmInference?.close()
        llmInference = null
        isInitialized = false
        _isDegraded.value = true
    }
}
