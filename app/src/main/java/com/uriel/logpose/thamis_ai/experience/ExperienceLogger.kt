package com.uriel.logpose.thamis_ai.experience

import android.util.Log

/**
 * Technical logger for the UX layer.
 */
class ExperienceLogger {
    fun logInteraction(style: ResponseStyle, outcome: String) {
        Log.d("THAMIS_UX", "Turn completed with style $style. Result: $outcome")
    }
}
