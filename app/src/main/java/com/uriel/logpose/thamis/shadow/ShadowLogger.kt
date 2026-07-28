package com.uriel.logpose.thamis.shadow

import android.util.Log
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Encargado de persistir y mostrar los resultados del Shadow Mode.
 */
object ShadowLogger {
    private const val TAG = "THAMIS_SHADOW"

    fun log(result: ShadowResult) {
        val status = if (result.isMatch) "✅ [MATCH]" else "⚠️ [DIVERGENCE]"
        
        LogPoseLogger.i("$status Input: '${result.input}'")
        Log.d(TAG, "   Legacy: ${result.legacyIntent}")
        Log.d(TAG, "   THAMIS: ${result.thamisDecision.winningEvaluation?.hypothesis?.candidateGoal?.category} (Conf: ${result.thamisDecision.winningEvaluation?.finalScore})")
        Log.d(TAG, "   Time: ${result.processingTimeMs}ms")
        
        if (!result.isMatch) {
            Log.w(TAG, "   Reasoning: ${result.thamisDecision.summary}")
            result.thamisDecision.winningEvaluation?.hypothesis?.evidences?.forEach { 
                Log.d(TAG, "      - Evidence: ${it.description} (${it.impact})")
            }
        }
    }
}
