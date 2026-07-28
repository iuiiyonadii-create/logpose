package com.uriel.logpose.thamis_ai.context

import android.util.Log

/**
 * Logs context transitions for debugging.
 */
class ContextLogger {
    fun logTransition(from: String, to: String) {
        Log.d("THAMIS_Context", "State changed: $from -> $to")
    }
}
