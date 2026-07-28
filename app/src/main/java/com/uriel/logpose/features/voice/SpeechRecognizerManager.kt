package com.uriel.logpose.features.voice

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Gestiona el ciclo de vida del SpeechRecognizer.
 * Optimizado para HyperOS: Menos re-creaciones, más estabilidad.
 */
class SpeechRecognizerManager(
    private val context: Context
) {

    private var recognizer: SpeechRecognizer? = null
    private var currentListener: RecognitionListener? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private var isActuallyListening = false

    fun initialize(listener: RecognitionListener) {
        this.currentListener = listener
        mainHandler.post { ensureRecognizerExists() }
    }

    private fun ensureRecognizerExists() {
        if (recognizer == null) {
            try {
                recognizer = SpeechRecognizer.createSpeechRecognizer(context)
                recognizer?.setRecognitionListener(currentListener)
                LogPoseLogger.i("Motor de voz: Objeto creado")
            } catch (e: Exception) {
                LogPoseLogger.e("Error creando recognizer: ${e.message}")
            }
        }
    }

    fun start() {
        mainHandler.post {
            ensureRecognizerExists()
            
            if (isActuallyListening) {
                recognizer?.stopListening()
            }
            
            try {
                val intent = createIntent()
                recognizer?.startListening(intent)
                isActuallyListening = true
                LogPoseLogger.d("Micro: Escuchando...")
            } catch (e: Exception) {
                LogPoseLogger.e("Fallo en startListening: ${e.message}")
                forceRecreation() // Si falla al arrancar, recreamos
            }
        }
    }

    fun onRecognitionError(error: Int) {
        isActuallyListening = false
        // Si el error es Busy (11), matamos el objeto para la próxima
        if (error == 11 || error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY) {
            forceRecreation()
        }
    }

    private fun forceRecreation() {
        LogPoseLogger.w("Motor de voz: Forzando recreación total")
        destroyInternal()
    }

    fun stop() {
        mainHandler.post {
            isActuallyListening = false
            try {
                recognizer?.stopListening()
            } catch (e: Exception) {}
        }
    }

    fun cancel() {
        mainHandler.post {
            isActuallyListening = false
            try {
                recognizer?.cancel()
            } catch (e: Exception) {}
        }
    }

    private fun destroyInternal() {
        isActuallyListening = false
        try {
            recognizer?.setRecognitionListener(null)
            recognizer?.destroy()
            recognizer = null
        } catch (e: Exception) {}
    }

    fun destroy() {
        mainHandler.post {
            destroyInternal()
            LogPoseLogger.i("Motor de voz: Destruido")
        }
    }

    fun onExternalError() {
        isActuallyListening = false
    }

    private fun createIntent(): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-AR")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            
            // Claude's PRO TIP: Sesgar el reconocimiento hacia palabras clave (API 33+)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                putExtra("android.speech.extra.BIASING_STRINGS", arrayOf(
                    "YSY A", "YSY", "Duki", "Bizarrap", "Música", "Spotify", 
                    "LogPose", "Thamis", "Navegar", "Llevame", "Abrir"
                ))
            }

            // Tiempos de escucha balanceados
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 1500L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000L)
        }
    }
}
