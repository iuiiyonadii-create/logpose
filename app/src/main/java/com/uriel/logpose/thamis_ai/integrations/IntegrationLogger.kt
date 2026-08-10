package com.uriel.logpose.thamis_ai.integrations

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Logs external connectivity events.
 */
class IntegrationLogger {
    fun logEvent(source: String, event: String) {
        LogPoseLogger.d("THAMIS_Integrations", "[$source] $event")
    }
}
