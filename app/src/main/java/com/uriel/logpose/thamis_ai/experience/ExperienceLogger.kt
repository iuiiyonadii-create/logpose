package com.uriel.logpose.thamis_ai.experience

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Technical logger for the UX layer.
 */
class ExperienceLogger {
    fun logInteraction(style: ResponseStyle, outcome: String) {
        LogPoseLogger.d("THAMIS_UX", "Turn completed with style $style. Result: $outcome")
    }
}
