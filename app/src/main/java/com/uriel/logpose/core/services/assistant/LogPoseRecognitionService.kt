package com.uriel.logpose.core.services.assistant

import android.speech.RecognitionService
import android.content.Intent

/**
 * Servicio requerido para cumplir con el contrato de asistente de Android.
 */
class LogPoseRecognitionService : RecognitionService() {
    override fun onStartListening(recognizerIntent: Intent?, listener: Callback?) {}
    override fun onCancel(listener: Callback?) {}
    override fun onStopListening(listener: Callback?) {}
}
