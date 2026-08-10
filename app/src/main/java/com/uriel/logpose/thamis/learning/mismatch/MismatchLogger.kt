package com.uriel.logpose.thamis.learning.mismatch

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Encargado de mostrar las divergencias fonéticas en el Logcat para auditoría.
 */
object MismatchLogger {
    private const val TAG = "THAMIS_MISMATCH"

    fun log(mismatch: VoiceMismatch, phoneticDistance: Float, correctionSource: String) {
        LogPoseLogger.w("$TAG --- DIVERGENCIA DETECTADA ---")
        LogPoseLogger.d(TAG, "   INPUT_EXPECTED: ${mismatch.expectedText}")
        LogPoseLogger.d(TAG, "   VOSK_OUTPUT:    ${mismatch.voskText}")
        LogPoseLogger.d(TAG, "   DISTANCE:       $phoneticDistance")
        LogPoseLogger.i(TAG, "   CORRECTION:     ${mismatch.normalizedText}")
        LogPoseLogger.d(TAG, "   CONFIDENCE:     ${mismatch.confidence}")
        LogPoseLogger.d(TAG, "   SOURCE:         $correctionSource")
        LogPoseLogger.d(TAG, "   TYPE:           ${mismatch.type}")
    }
}
