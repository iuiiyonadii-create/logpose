package com.uriel.logpose.features.voice

import android.content.Context
import com.uriel.logpose.core.compat.core.LogPoseLogger
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.StorageService
import java.io.IOException

/**
 * Motor de voz independiente (Vosk).
 * Funciona 100% offline y no depende de Google.
 */
class VoskRecognizerManager(private val context: Context) {

    private var model: Model? = null
    private var recognizer: Recognizer? = null
    private var isInitialized = false

    fun initialize(onReady: () -> Unit) {
        StorageService.unpack(context, "model-es", "model",
            { model: Model ->
                this.model = model
                isInitialized = true
                LogPoseLogger.i("Vosk: Modelo cargado con éxito")
                onReady()
            },
            { exception: IOException ->
                LogPoseLogger.e("Vosk: Error cargando modelo: ${exception.message}")
            }
        )
    }

    fun start(listener: (String) -> Unit) {
        if (!isInitialized || model == null) {
            LogPoseLogger.w("Vosk: No inicializado")
            return
        }

        try {
            // Configuración del reconocedor con el modelo
            recognizer = Recognizer(model, 16000.0f)
            LogPoseLogger.i("Vosk: Escucha iniciada")
            // Aquí se integraría con el flujo de AudioRecord de Android
        } catch (e: Exception) {
            LogPoseLogger.e("Vosk: Error al iniciar: ${e.message}")
        }
    }

    fun stop() {
        recognizer?.reset()
        LogPoseLogger.i("Vosk: Escucha detenida")
    }
}
