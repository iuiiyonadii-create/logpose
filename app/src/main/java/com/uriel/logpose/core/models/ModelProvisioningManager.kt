package com.uriel.logpose.core.models

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
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
 * Gestor de almacenamiento seguro para credenciales y tokens (Hugging Face).
 */
object SecureTokenStorage {
    private const val PREFS_FILE = "secure_tokens_pref"
    private const val KEY_HF_TOKEN = "hf_access_token"

    private fun getPrefs(context: Context): SharedPreferences {
        return try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            EncryptedSharedPreferences.create(
                context,
                PREFS_FILE,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        }
    }

    fun saveHfToken(context: Context, token: String) {
        try {
            getPrefs(context).edit().putString(KEY_HF_TOKEN, token.trim()).apply()
        } catch (e: Exception) {
            context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
                .edit().putString(KEY_HF_TOKEN, token.trim()).apply()
        }
    }

    fun getHfToken(context: Context): String {
        return try {
            getPrefs(context).getString(KEY_HF_TOKEN, "") ?: ""
        } catch (e: Exception) {
            context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
                .getString(KEY_HF_TOKEN, "") ?: ""
        }
    }
}

/**
 * ModelDescriptor: Metadatos para el aprovisionamiento de modelos locales de IA y STT.
 */
data class ModelDescriptor(
    val id: String,
    val displayName: String,
    val filename: String,
    val targetDirProvider: (Context) -> File,
    val downloadUrl: String = "",
    val requiresAuth: Boolean = false,
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
    val errorMessage: String? = null,
    val hfToken: String = "",
    val isHfTokenConfigured: Boolean = false
) {
    val missingItems: List<ModelItemStatus>
        get() = items.filter { !it.isPresent }

    val hasAutoDownloadableMissing: Boolean
        get() = missingItems.any { it.descriptor.isAutoDownloadable }
}

sealed class DownloadResult {
    object Success : DownloadResult()
    data class AuthError(val code: Int, val message: String) : DownloadResult()
    data class Failure(val message: String) : DownloadResult()
}

/**
 * ModelProvisioningManager v2.0: Gestor autónomo de provisión y descarga de modelos cognitivos.
 * Soporta descarga pública para Whisper y autenticación Bearer Token para modelos Hugging Face protegidos (Gemma).
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
            downloadUrl = "https://huggingface.co/csukuangfj/sherpa-onnx-whisper-tiny.en/resolve/main/tiny.en-encoder.int8.onnx",
            requiresAuth = false
        ),
        ModelDescriptor(
            id = "whisper_decoder",
            displayName = "Whisper Decoder (STT)",
            filename = "tiny.en-decoder.int8.onnx",
            targetDirProvider = { File(it.getExternalFilesDir(null), "Models/whisper") },
            downloadUrl = "https://huggingface.co/csukuangfj/sherpa-onnx-whisper-tiny.en/resolve/main/tiny.en-decoder.int8.onnx",
            requiresAuth = false
        ),
        ModelDescriptor(
            id = "whisper_tokens",
            displayName = "Whisper Tokens (Vocabulario)",
            filename = "tiny.en-tokens.txt",
            targetDirProvider = { File(it.getExternalFilesDir(null), "Models/whisper") },
            downloadUrl = "https://huggingface.co/csukuangfj/sherpa-onnx-whisper-tiny.en/resolve/main/tiny.en-tokens.txt",
            requiresAuth = false
        ),
        ModelDescriptor(
            id = "llm_brain",
            displayName = "Cerebro Neuronal Gemma 3 (LLM)",
            filename = "staff_brain.bin",
            targetDirProvider = { File(it.getExternalFilesDir(null), "llm") },
            downloadUrl = "https://huggingface.co/litert-community/Gemma3-1B-IT/resolve/main/gemma3-1b-it-int4.task",
            requiresAuth = true
        )
    )

    private val _state = MutableStateFlow(ModelProvisioningState())
    val state: StateFlow<ModelProvisioningState> = _state.asStateFlow()

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .followRedirects(true)
            .build()
    }

    fun saveHfToken(context: Context, token: String) {
        SecureTokenStorage.saveHfToken(context, token)
        val cleanToken = token.trim()
        _state.update {
            it.copy(
                hfToken = cleanToken,
                isHfTokenConfigured = cleanToken.isNotBlank(),
                errorMessage = null
            )
        }
        LogPoseLogger.i(TAG, "🔑 Hugging Face Token actualizado de forma segura.")
    }

    fun getHfToken(context: Context): String {
        return SecureTokenStorage.getHfToken(context)
    }

    /**
     * Inspecciona el sistema de archivos y actualiza el estado de los modelos.
     */
    fun checkModels(context: Context): ModelProvisioningState {
        val token = getHfToken(context)
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
            hfToken = token,
            isHfTokenConfigured = token.isNotBlank()
        )
        _state.value = newState

        LogPoseLogger.d(TAG, "Diagnóstico de modelos: ${checkedItems.count { it.isPresent }}/${checkedItems.size} presentes. HF Token configurado: ${token.isNotBlank()}")
        return newState
    }

    /**
     * Inicia la descarga en background de los modelos faltantes.
     */
    fun startDownload(context: Context) {
        if (_state.value.isDownloading) return

        scope.launch {
            _state.update { 
                it.copy(
                    isDownloading = true, 
                    errorMessage = null,
                    statusMessage = "Iniciando aprovisionamiento..."
                ) 
            }

            val currentHfToken = getHfToken(context)
            val missing = checkModels(context).missingItems
            val autoMissing = missing.filter { it.descriptor.isAutoDownloadable }

            if (autoMissing.isEmpty()) {
                val allReady = missing.isEmpty()
                _state.update {
                    it.copy(
                        isDownloading = false,
                        allReady = allReady,
                        statusMessage = if (allReady) "Todos los modelos ya están disponibles." else "Modelos listos."
                    )
                }
                return@launch
            }

            // Validar si algún modelo requiere token y falta
            val needsAuthAndMissingToken = autoMissing.any { it.descriptor.requiresAuth } && currentHfToken.isBlank()
            if (needsAuthAndMissingToken) {
                val downloadableWithoutAuth = autoMissing.filter { !it.descriptor.requiresAuth }
                if (downloadableWithoutAuth.isEmpty()) {
                    _state.update {
                        it.copy(
                            isDownloading = false,
                            errorMessage = "Falta configurar tu token de Hugging Face para descargar el modelo LLM.",
                            statusMessage = "Token requerido"
                        )
                    }
                    return@launch
                }
            }

            var success = true
            val totalCount = autoMissing.size

            for ((index, item) in autoMissing.withIndex()) {
                val modelName = item.descriptor.displayName
                val targetFile = item.targetFile
                val tempFile = File(targetFile.parentFile, "${targetFile.name}.tmp")

                if (item.descriptor.requiresAuth && currentHfToken.isBlank()) {
                    LogPoseLogger.w(TAG, "Omitiendo $modelName por falta de Hugging Face Token.")
                    _state.update {
                        it.copy(
                            isDownloading = false,
                            errorMessage = "Falta configurar tu token de Hugging Face para descargar $modelName.",
                            statusMessage = "Falta token HF"
                        )
                    }
                    success = false
                    break
                }

                _state.update {
                    it.copy(
                        currentModelName = modelName,
                        currentProgress = 0f,
                        totalProgress = index.toFloat() / totalCount.toFloat(),
                        statusMessage = "Descargando $modelName (${index + 1}/$totalCount)..."
                    )
                }

                val authHeader = if (item.descriptor.requiresAuth) "Bearer $currentHfToken" else null

                val result = downloadFile(
                    url = item.descriptor.downloadUrl,
                    destination = tempFile,
                    authHeader = authHeader,
                    onProgress = { fileProgress ->
                        _state.update {
                            it.copy(
                                currentProgress = fileProgress,
                                totalProgress = (index + fileProgress) / totalCount.toFloat()
                            )
                        }
                    }
                )

                when (result) {
                    is DownloadResult.Success -> {
                        if (targetFile.exists()) targetFile.delete()
                        val renamed = tempFile.renameTo(targetFile)
                        if (!renamed) {
                            tempFile.copyTo(targetFile, overwrite = true)
                            tempFile.delete()
                        }
                        LogPoseLogger.i(TAG, "✅ Descargado exitosamente: ${item.descriptor.filename} -> ${targetFile.absolutePath}")
                    }
                    is DownloadResult.AuthError -> {
                        success = false
                        tempFile.delete()
                        LogPoseLogger.e(TAG, "❌ Error de autenticación (${result.code}) en: ${item.descriptor.filename}")
                        _state.update {
                            it.copy(
                                isDownloading = false,
                                errorMessage = "Token inválido o falta aceptar la licencia del modelo en Hugging Face (huggingface.co/litert-community/Gemma3-1B-IT).",
                                statusMessage = "Error de autorización (${result.code})"
                            )
                        }
                        break
                    }
                    is DownloadResult.Failure -> {
                        success = false
                        tempFile.delete()
                        LogPoseLogger.e(TAG, "❌ Falló la descarga de: ${item.descriptor.filename} - ${result.message}")
                        _state.update {
                            it.copy(
                                isDownloading = false,
                                errorMessage = "Error al descargar $modelName. ${result.message}",
                                statusMessage = "Descarga interrumpida"
                            )
                        }
                        break
                    }
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
                        val neuralSuccess = ThamisNeuralEngine.initialize(context)
                        LogPoseLogger.i(TAG, "✅ Motores Whisper y NeuralEngine inicializados post-descarga (LLM Online: $neuralSuccess).")
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
        authHeader: String? = null,
        onProgress: (Float) -> Unit
    ): DownloadResult = withContext(Dispatchers.IO) {
        try {
            val requestBuilder = Request.Builder().url(url)
            if (!authHeader.isNullOrBlank()) {
                requestBuilder.addHeader("Authorization", authHeader)
            }
            val request = requestBuilder.build()

            httpClient.newCall(request).execute().use { response ->
                if (response.code == 401 || response.code == 403) {
                    LogPoseLogger.e(TAG, "HTTP Auth Error: ${response.code} en $url")
                    return@withContext DownloadResult.AuthError(response.code, "Acceso denegado (HTTP ${response.code})")
                }

                if (!response.isSuccessful) {
                    LogPoseLogger.e(TAG, "HTTP error: ${response.code} en $url")
                    return@withContext DownloadResult.Failure("HTTP ${response.code}: ${response.message}")
                }

                val body = response.body ?: return@withContext DownloadResult.Failure("Respuesta vacía del servidor")
                val contentLength = body.contentLength()

                body.byteStream().use { input ->
                    FileOutputStream(destination).use { output ->
                        val buffer = ByteArray(16384)
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
                DownloadResult.Success
            }
        } catch (e: Exception) {
            LogPoseLogger.e(TAG, "Excepción durante descarga de $url: ${e.message}")
            DownloadResult.Failure(e.localizedMessage ?: "Error de red")
        }
    }
}
