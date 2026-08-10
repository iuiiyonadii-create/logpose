package com.uriel.logpose.audio.perception

import android.media.AudioManager
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Orquestador del diagnóstico auditivo de THAMIS.
 */
object AudioDiagnosticManager {
    private const val TAG = "THAMIS_AUDIO"

    fun logDiagnostic(diagnostic: AudioDiagnostic) {
        LogPoseLogger.i(TAG, "--- AUDIO_DIAGNOSTIC ---")
        LogPoseLogger.d(TAG, "   Input: ${diagnostic.inputSource}")
        LogPoseLogger.d(TAG, "   Bluetooth: ${diagnostic.isBluetooth}")
        LogPoseLogger.d(TAG, "   SCO: ${diagnostic.isScoActive}")
        LogPoseLogger.d(TAG, "   Noise: ${diagnostic.noiseLevel}")
        LogPoseLogger.d(TAG, "   Voice: ${diagnostic.voiceLevel}")
        
        val quality = AudioQualityAnalyzer.calculateQuality(
            diagnostic.noiseLevel, 
            diagnostic.voiceLevel
        )
        LogPoseLogger.i(TAG, "   QUALITY: $quality")
    }
}
