package com.uriel.logpose.features.voice

import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import com.uriel.logpose.core.compat.core.LogPoseLogger

class SpeechRecognitionListener(
    private val onTextReceived: (String) -> Unit,
    private val onReady: () -> Unit = {},
    private val onRecognitionError: (Int) -> Unit = {},
    private val onFinished: () -> Unit = {}
) : RecognitionListener {

    override fun onReadyForSpeech(params: Bundle?) {
        LogPoseLogger.i("✅ Micrófono listo")
        onReady()
    }

    override fun onBeginningOfSpeech() {
        LogPoseLogger.i("👂 Escuchando...")
    }

    override fun onRmsChanged(rmsdB: Float) {}
    override fun onBufferReceived(buffer: ByteArray?) {}
    override fun onEndOfSpeech() {}

    override fun onError(error: Int) {
        // Al haber error, liberamos el audio de inmediato para que no se trabe
        VoiceManager.notifyError(error)
        onRecognitionError(error)
        onFinished()
    }

    override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val text = matches?.firstOrNull()
        
        onFinished() // Liberamos el hardware de inmediato (SCO OFF)

        if (text != null) {
            onTextReceived(text)
        } else {
            VoiceManager.notifyError(SpeechRecognizer.ERROR_NO_MATCH)
        }
    }

    override fun onPartialResults(partialResults: Bundle?) {
        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        matches?.firstOrNull()?.let { 
            // Log parcial desactivado para no saturar Logcat en segundo plano, 
            // activar solo para debugging intensivo.
        }
    }

    override fun onEvent(eventType: Int, params: Bundle?) {}
}
