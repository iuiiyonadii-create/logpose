package com.uriel.logpose.thamis.validation

import com.thamis.lab.core.contracts.intent.Intent
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
        
        LogPoseLogger.d(TAG, "EVENT:")
        LogPoseLogger.d(TAG, "   Input: '${event.rawInput}'")
        LogPoseLogger.d(TAG, "   THAMIS: ${event.thamisDecision.winningEvaluation?.hypothesis?.candidateGoal?.category} (Conf: ${event.thamisDecision.winningEvaluation?.finalScore})")
        LogPoseLogger.d(TAG, "   Legacy: ${event.legacyIntent}")
        LogPoseLogger.d(TAG, "   RESULT: $result")
        
        if (!event.shadowResult.isMatch && event.legacyIntent == Intent.UNKNOWN) {
            LogPoseLogger.i(TAG, "   [THAMIS_BETTER] El sistema cognitivo detectó una intención que el legado ignoró.")
        }
    }
}
