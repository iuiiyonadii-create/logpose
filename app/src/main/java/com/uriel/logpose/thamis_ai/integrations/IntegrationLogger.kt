package com.uriel.logpose.thamis_ai.integrations

import android.util.Log

/**
 * Logs external connectivity events.
 */
class IntegrationLogger {
    fun logEvent(source: String, event: String) {
        Log.d("THAMIS_Integrations", "[$source] $event")
    }
}
