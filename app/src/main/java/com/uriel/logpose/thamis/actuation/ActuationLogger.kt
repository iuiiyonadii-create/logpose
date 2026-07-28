package com.uriel.logpose.thamis.actuation

import android.util.Log
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.cognitive.model.ThamisDecision

/**
 * Logger especializado para la capa de actuación de THAMIS.
 */
object ActuationLogger {
    private const val TAG = "THAMIS_ACTUATION"

    fun log(decision: ThamisDecision, result: ActuationResult) {
        val status = if (result.success) "GRANTED" else "DENIED"
        val eval = decision.winningEvaluation
        
        LogPoseLogger.i("[THAMIS_DECISION] Goal=${eval?.hypothesis?.candidateGoal?.category} Confidence=${eval?.finalScore} Risk=${eval?.risk?.level} Action=${if (result.success) "EXECUTE" else "BLOCK"}")
        LogPoseLogger.i("[THAMIS_AUTHORITY] Domain=${eval?.hypothesis?.candidateGoal?.category} Permission=$status Reason=${result.reason}")
        
        if (result.success) {
            LogPoseLogger.i("[THAMIS_ACTUATOR] Action=${result.action}")
        }
    }
}
