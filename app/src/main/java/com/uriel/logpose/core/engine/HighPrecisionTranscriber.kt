package com.uriel.logpose.core.engine

import com.uriel.logpose.core.app.LogPoseApplication
import com.uriel.logpose.core.compat.core.LogPoseLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * HighPrecisionTranscriber v5.0: Migración a Sherpa-ONNX Local.
 * Elimina la dependencia de red y servidores externos para transcripción Staff.
 */
object HighPrecisionTranscriber {

    suspend fun transcribeAudio(pcmData: ShortArray): String? = withContext(Dispatchers.Default) {
        LogPoseLogger.d("HPTranscriber: Procesando buffer local con Sherpa-ONNX...")

        try {
            // Convertir ShortArray a FloatArray (-1.0f a 1.0f) para Sherpa
            val floatSamples = FloatArray(pcmData.size) { i -> pcmData[i] / 32768.0f }
            
            val transcription = LogPoseApplication.instance.sherpaEngine.transcribe(floatSamples)
            
            if (transcription.isNotBlank()) {
                LogPoseLogger.i("HPTranscriber: Sherpa local resolvió -> '$transcription'")
                return@withContext transcription
            }
        } catch (e: Exception) {
            LogPoseLogger.e("HPTranscriber: Error en motor local -> ${e.localizedMessage}")
        }
        return@withContext null
    }
}
