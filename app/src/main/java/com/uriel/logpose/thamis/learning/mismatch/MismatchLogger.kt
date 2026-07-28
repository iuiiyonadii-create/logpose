package com.uriel.logpose.thamis.learning.mismatch

import android.util.Log
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Encargado de mostrar las divergencias fonéticas en el Logcat para auditoría.
 */
object MismatchLogger {
    private const val TAG = "THAMIS_MISMATCH"

    fun log(mismatch: VoiceMismatch, phoneticDistance: Float, correctionSource: String) {
        LogPoseLogger.w("$TAG --- DIVERGENCIA DETECTADA ---")
        Log.d(TAG, "   INPUT_EXPECTED: ${mismatch.expectedText}")
        Log.d(TAG, "   VOSK_OUTPUT:    ${mismatch.voskText}")
        Log.d(TAG, "   DISTANCE:       $phoneticDistance")
        Log.i(TAG, "   CORRECTION:     ${mismatch.normalizedText}")
        Log.d(TAG, "   CONFIDENCE:     ${mismatch.confidence}")
        Log.d(TAG, "   SOURCE:         $correctionSource")
        Log.d(TAG, "   TYPE:           ${mismatch.type}")
    }
}
