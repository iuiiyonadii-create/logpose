package com.uriel.logpose.thamis_ai.proactive

import android.util.Log

/**
 * Identifies moments where proactive help adds value.
 */
class OpportunityDetector {

    fun detect(context: Map<String, Any>): ProactiveAction? {
        val bluetoothConnected = context["bluetooth_connected"] as? Boolean ?: false
        val batteryLevel = context["battery_level"] as? Int ?: 100
        
        return when {
            bluetoothConnected && (context["music_played_often"] == true) -> ProactiveAction.SUGGEST
            batteryLevel < 15 -> ProactiveAction.WARN
            else -> null
        }
    }
}
