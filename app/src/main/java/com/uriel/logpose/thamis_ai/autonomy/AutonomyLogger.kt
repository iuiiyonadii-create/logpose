package com.uriel.logpose.thamis_ai.autonomy

import android.util.Log

/**
 * Logs autonomous decisions and their outcomes.
 */
class AutonomyLogger {
    fun logAction(action: String, approved: Boolean) {
        Log.d("THAMIS_Autonomy", "Action $action was ${if (approved) "executed" else "rejected"}")
    }
}
