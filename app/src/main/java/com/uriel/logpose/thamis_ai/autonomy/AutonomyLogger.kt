package com.uriel.logpose.thamis_ai.autonomy

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Logs autonomous decisions and their outcomes.
 */
class AutonomyLogger {
    fun logAction(action: String, approved: Boolean) {
        LogPoseLogger.d("THAMIS_Autonomy", "Action $action was ${if (approved) "executed" else "rejected"}")
    }
}
