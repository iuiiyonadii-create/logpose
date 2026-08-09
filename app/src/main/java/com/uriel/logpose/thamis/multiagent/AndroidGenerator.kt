package com.uriel.logpose.thamis.multiagent

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * FASE 27.3 — THAMIS LAB EXECUTION LAYER
 * Generador de intenciones a nivel de sistema Android.
 */
object AndroidGenerator {

    data class AndroidAction(
        val type: ActionType,
        val packageName: String? = null,
        val command: String? = null,
        val extraData: Map<String, String> = emptyMap()
    )

    enum class ActionType { 
        OPEN_APP, 
        CLOSE_APP, 
        SET_SETTINGS, 
        TRIGGER_INTENT,
        ROUTE_AUDIO
    }

    /**
     * Traduce una decisión técnica en una acción ejecutable por el sistema operativo Android.
     */
    fun generate(task: String): AndroidAction {
        LogPoseLogger.d("AndroidGenerator: Generando acción ejecutable para '$task'")
        val lower = task.lowercase()
        
        return when {
            lower.contains("whatsapp") -> 
                AndroidAction(ActionType.OPEN_APP, packageName = "com.whatsapp")
            
            lower.contains("spotify") || lower.contains("música") || lower.contains("musica") -> 
                AndroidAction(ActionType.OPEN_APP, packageName = "com.spotify.music")
            
            lower.contains("maps") || lower.contains("gps") || lower.contains("ir a") || lower.contains("navegar") -> 
                AndroidAction(ActionType.TRIGGER_INTENT, packageName = "com.google.android.apps.maps", command = "GEO_NAVIGATE", extraData = mapOf("destination" to task))
            
            lower.contains("silence") || lower.contains("callar") || lower.contains("no molestar") -> 
                AndroidAction(ActionType.SET_SETTINGS, command = "DND_ON")

            lower.contains("volumen máximo") || lower.contains("volumen maximo") -> 
                AndroidAction(ActionType.SET_SETTINGS, command = "VOLUME_MAX")
            
            lower.contains("bajar volumen") || lower.contains("menos volumen") -> 
                AndroidAction(ActionType.SET_SETTINGS, command = "VOLUME_DOWN")

            lower.contains("sco") || lower.contains("casco") -> 
                AndroidAction(ActionType.ROUTE_AUDIO, command = "SCO_HEADSET_ON")

            lower.contains("a2dp") || lower.contains("parlante") -> 
                AndroidAction(ActionType.ROUTE_AUDIO, command = "A2DP_MUSIC_ON")

            else -> AndroidAction(ActionType.TRIGGER_INTENT, command = task)
        }
    }
}
