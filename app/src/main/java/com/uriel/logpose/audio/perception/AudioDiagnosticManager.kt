package com.uriel.logpose.audio.perception

import android.media.AudioManager
import android.util.Log
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Orquestador del diagnóstico auditivo de THAMIS.
 */
object AudioDiagnosticManager {
    private const val TAG = "THAMIS_AUDIO"

    fun logDiagnostic(diagnostic: AudioDiagnostic) {
        LogPoseLogger.i("$TAG --- AUDIO_DIAGNOSTIC ---")
        Log.d(TAG, "   Input: ${diagnostic.inputSource}")
        Log.d(TAG, "   Bluetooth: ${diagnostic.isBluetooth}")
        Log.d(TAG, "   SCO: ${diagnostic.isScoActive}")
        Log.d(TAG, "   Noise: ${diagnostic.noiseLevel}")
        Log.d(TAG, "   Voice: ${diagnostic.voiceLevel}")
        
        val quality = AudioQualityAnalyzer.calculateQuality(
            diagnostic.noiseLevel, 
            diagnostic.voiceLevel
        )
        Log.i(TAG, "   QUALITY: $quality")
    }
}
