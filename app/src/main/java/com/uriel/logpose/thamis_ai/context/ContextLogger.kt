package com.uriel.logpose.thamis_ai.context

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Logs context transitions for debugging.
 */
class ContextLogger {
    fun logTransition(from: String, to: String) {
        LogPoseLogger.d("THAMIS_Context", "State changed: $from -> $to")
    }
}
