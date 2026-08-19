package com.uriel.logpose.thamis.voiceexperience.priority

import com.uriel.logpose.thamis.voiceexperience.model.VoicePriority

/**
 * Motor de asignación de prioridades para mensajes vocales.
 */
object VoicePriorityEngine {
    
    private val priorityMap = mapOf(
        "EMERGENCY" to VoicePriority.EMERGENCY,
        "TURN" to VoicePriority.NAVIGATION,
        "CALL_INCOMING" to VoicePriority.CALL,
        "SPEED_ALARM" to VoicePriority.SAFETY,
        "MUSIC_INFO" to VoicePriority.MULTIMEDIA
    )

    fun resolvePriority(category: String): VoicePriority {
        return priorityMap[category.uppercase()] ?: VoicePriority.INFORMATION
    }
}
