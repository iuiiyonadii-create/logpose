package com.uriel.logpose.thamis.validation

import com.thamis.lab.core.contracts.intent.Intent
import android.util.Log
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Registra la actividad de validación en tiempo real para Logcat.
 */
object DrivingTestLogger {
    private const val TAG = "THAMIS_VALIDATION"

    fun logSessionStart(session: ValidationSession) {
        LogPoseLogger.i("$TAG --- SESSION_START ID: ${session.sessionId} ---")
    }

    fun logEvent(event: ValidationEvent) {
        val result = if (event.shadowResult.isMatch) "MATCH" else "DIVERGENCE"
        
        Log.d(TAG, "EVENT:")
        Log.d(TAG, "   Input: '${event.rawInput}'")
        Log.d(TAG, "   THAMIS: ${event.thamisDecision.winningEvaluation?.hypothesis?.candidateGoal?.category} (Conf: ${event.thamisDecision.winningEvaluation?.finalScore})")
        Log.d(TAG, "   Legacy: ${event.legacyIntent}")
        Log.d(TAG, "   RESULT: $result")
        
        if (!event.shadowResult.isMatch && event.legacyIntent == Intent.UNKNOWN) {
            Log.i(TAG, "   [THAMIS_BETTER] El sistema cognitivo detectó una intención que el legado ignoró.")
        }
    }
}
