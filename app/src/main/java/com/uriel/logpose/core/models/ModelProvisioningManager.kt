package com.uriel.logpose.core.models

import android.content.Context
import com.uriel.logpose.core.app.LogPoseApplication
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.intelligence.ThamisNeuralEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

/**
 * ModelDescriptor: Metadatos para el aprovisionamiento de modelos locales de IA y STT.
 */
data class ModelDescriptor(
    val id: String,
    val displayName: String,
    val filename: String,
    val targetDirProvider: (Context) -> File,
    val downloadUrl: String = "",
    val isCritical: Boolean = true
) {
    val isAutoDownloadable: Boolean
        get() = downloadUrl.isNotBlank()

    fun getTargetFile(context: Context): File {
        val dir = targetDirProvider(context)
        if (!dir.exists()) dir.mkdirs()
        return File(dir, filename)
    }
}

data class ModelItemStatus(
    val descriptor: ModelDescriptor,
    val isPresent: Boolean,
    val targetFile: File
)

data class ModelProvisioningState(
    val items: List<ModelItemStatus> = emptyList(),
    val allReady: Boolean = false,
    val isDownloading: Boolean = false,
    val currentModelName: String = "",
    val currentProgress: Float = 0f,
    val totalProgress: Float = 0f,
    val statusMessage: String = "",
    val errorMessage: String? = null
) {
    val missingItems: List<ModelItemStatus>
        get() = items.filter { !it.isPresent }

    val hasAutoDownloadableMissing: Boolean
        get() = missingItems.any { it.descriptor.isAutoDownloadable }
}

/**
 * ModelProvisioningManager v1.0: Gestor autónomo de provisión y descarga de modelos cognitivos.
 */
object ModelProvisioningManager {

    private const val TAG = "ModelProvisioner"
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    val REQUIRED_MODELS = listOf(
        ModelDescriptor(
            id = "whisper_encoder",
            displayName = "Whisper Encoder (STT)",
            filename = "tiny.en-encoder.int8.onnx",
            targetDirProvider = { File(it.getExternalFilesDir(null), "Models/whisper") },
            downloadUrl = "https://huggingface.co/csukuangfj/sherpa-onnx-whisper-tiny.en/resolve/main/tiny.en-encoder.int8.onnx"
        ),
        ModelDescriptor(
            id = "whisper_decoder",
            displayName = "Whisper Decoder (STT)",
            filename = "tiny.en-decoder.int8.onnx",
            targetDirProvider = { File(it.getExternalFilesDir(null), "Models/whisper") },
            downloadUrl = "https://huggingface.co/csukuangfj/sherpa-onnx-whisper-tiny.en/resolve/main/tiny.en-decoder.int8.onnx"
        ),
        ModelDescriptor(
            id = "whisper_tokens",
            displayName = "Whisper Tokens (Vocabulario)",
            filename = "tiny.en-tokens.txt",
            targetDirProvider = { File(it.getExternalFilesDir(null), "Models/whisper") },
            downloadUrl = "https://huggingface.co/csukuangfj/sherpa-onnx-whisper-tiny.en/resolve/main/tiny.en-tokens.txt"
        ),
        ModelDescriptor(
            id = "llm_brain",
            displayName = "Cerebro Neuronal Staff (LLM - Manual)",
            filename = "staff_brain.bin",
            targetDirProvider = { File(it.getExternalFilesDir(null), "llm") },
            downloadUrl = "" // Provisión manual vía USB / adb
        )
    )

    private val _state = MutableStateFlow(ModelProvisioningState())
    val state: StateFlow<ModelProvisioningState> = _state.asStateFlow()

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .followRedirects(true)
            .build()
    }

    /**
     * Inspecciona el sistema de archivos y actualiza el estado de los modelos.
     */
    fun checkModels(context: Context): ModelProvisioningState {
        val checkedItems = REQUIRED_MODELS.map { descriptor ->
            val targetFile = descriptor.getTargetFile(context)
            // Chequeo de variantes compatibles para Whisper
            val exists = when (descriptor.id) {
                "whisper_encoder" -> targetFile.exists() || 
                        File(targetFile.parentFile, "tiny-encoder.int8.onnx").exists() ||
                        File(targetFile.parentFile, "encoder.onnx").exists()
                "whisper_decoder" -> targetFile.exists() || 
                        File(targetFile.parentFile, "tiny-decoder.int8.onnx").exists() ||
                        File(targetFile.parentFile, "decoder.onnx").exists()
                "whisper_tokens" -> targetFile.exists() || 
                        File(targetFile.parentFile, "tiny-tokens.txt").exists() ||
                        File(targetFile.parentFile, "tokens.txt").exists()
                else -> targetFile.exists()
            }
            ModelItemStatus(
                descriptor = descriptor,
                isPresent = exists,
                targetFile = targetFile
            )
        }

        val allReady = checkedItems.all { it.isPresent }
        val newState = _state.value.copy(
            items = checkedItems,
            allReady = allReady,
            statusMessage = if (allReady) "Todos los modelos están listos" else "Faltan ${checkedItems.count { !it.isPresent }} modelos"
        )
        _state.value = newState

        if (!allReady) {
            val missingSummary = checkedItems.filter { !it.isPresent }
                .joinToString(", ") { "${it.descriptor.displayName} -> ${it.targetFile.absolutePath}" }
            LogPoseLogger.w(TAG, "⚠️ Modelos ausentes detectados: $missingSummary")
        } else {
            LogPoseLogger.i(TAG, "✅ Verificación de modelos completa: Todos presentes.")
        }

        return newState
    }

    /**
     * Inicia la descarga asíncrona de los modelos faltantes.
     */
    fun startDownload(context: Context) {
        if (_state.value.isDownloading) return

        scope.launch {
            _state.update {
                it.copy(
                    isDownloading = true,
                    errorMessage = null,
                    statusMessage = "Iniciando descarga de modelos..."
                )
            }

            val missing = checkModels(context).missingItems
            val autoMissing = missing.filter { it.descriptor.isAutoDownloadable }
            if (autoMissing.isEmpty()) {
                val allReady = missing.isEmpty()
                _state.update {
                    it.copy(
                        isDownloading = false,
                        allReady = allReady,
                        statusMessage = if (allReady) "Todos los modelos ya están disponibles." else "Modelos Whisper listos. Copia staff_brain.bin a la carpeta llm/"
                    )
                }
                return@launch
            }

            var success = true
            val totalCount = autoMissing.size

            for ((index, item) in autoMissing.withIndex()) {
                val modelName = item.descriptor.displayName
                val targetFile = item.targetFile
                val tempFile = File(targetFile.parentFile, "${targetFile.name}.tmp")

                _state.update {
                    it.copy(
                        currentModelName = modelName,
                        currentProgress = 0f,
                        totalProgress = index.toFloat() / totalCount.toFloat(),
                        statusMessage = "Descargando $modelName (${index + 1}/$totalCount)..."
                    )
                }

                val downloaded = downloadFile(
                    url = item.descriptor.downloadUrl,
                    destination = tempFile,
                    onProgress = { fileProgress ->
                        _state.update {
                            it.copy(
                                currentProgress = fileProgress,
                                totalProgress = (index + fileProgress) / totalCount.toFloat()
                            )
                        }
                    }
                )

                if (downloaded) {
                    if (targetFile.exists()) targetFile.delete()
                    val renamed = tempFile.renameTo(targetFile)
                    if (!renamed) {
                        tempFile.copyTo(targetFile, overwrite = true)
                        tempFile.delete()
                    }
                    LogPoseLogger.i(TAG, "✅ Descargado: ${item.descriptor.filename} -> ${targetFile.absolutePath}")
                } else {
                    success = false
                    tempFile.delete()
                    LogPoseLogger.e(TAG, "❌ Falló la descarga de: ${item.descriptor.filename}")
                    _state.update {
                        it.copy(
                            isDownloading = false,
                            errorMessage = "Error al descargar $modelName. Verifica tu conexión.",
                            statusMessage = "Descarga interrumpida"
                        )
                    }
                    break
                }
            }

            if (success) {
                checkModels(context)
                _state.update {
                    it.copy(
                        isDownloading = false,
                        totalProgress = 1.0f,
                        currentProgress = 1.0f,
                        statusMessage = "Modelos descargados con éxito. Despertando motores..."
                    )
                }

                // Re-inicialización automática de motores
                withContext(Dispatchers.IO) {
                    try {
                        LogPoseApplication.instance.whisperEngine.initEngine()
                        ThamisNeuralEngine.initialize(context)
                        LogPoseLogger.i(TAG, "✅ Motores Whisper y NeuralEngine inicializados post-descarga.")
                    } catch (e: Exception) {
                        LogPoseLogger.e(TAG, "Error re-inicializando motores: ${e.message}")
                    }
                }
            }
        }
    }

    private suspend fun downloadFile(
        url: String,
        destination: File,
        onProgress: (Float) -> Unit
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(url).build()
            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    LogPoseLogger.e(TAG, "HTTP error: ${response.code} en $url")
                    return@withContext false
                }

                val body = response.body ?: return@withContext false
                val contentLength = body.contentLength()

                body.byteStream().use { input ->
                    FileOutputStream(destination).use { output ->
                        val buffer = ByteArray(8192)
                        var bytesRead: Int
                        var totalBytesRead: Long = 0

                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            totalBytesRead += bytesRead
                            if (contentLength > 0) {
                                val progress = (totalBytesRead.toDouble() / contentLength.toDouble()).toFloat()
                                onProgress(progress)
                            }
                        }
                        output.flush()
                    }
                }
                true
            }
        } catch (e: Exception) {
            LogPoseLogger.e(TAG, "Excepción durante descarga de $url: ${e.message}")
            false
        }
    }
}
