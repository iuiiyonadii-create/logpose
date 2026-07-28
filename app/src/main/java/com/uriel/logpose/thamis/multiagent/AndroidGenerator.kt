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
        val command: String? = null
    )

    enum class ActionType { OPEN_APP, CLOSE_APP, SET_SETTINGS, TRIGGER_INTENT }

    /**
     * Traduce una decisión técnica en una acción ejecutable por el SO.
     */
    fun generate(task: String): AndroidAction {
        LogPoseLogger.d("AndroidGenerator: Generando acción para '$task'")
        
        return when {
            task.contains("WhatsApp", ignoreCase = true) -> 
                AndroidAction(ActionType.OPEN_APP, "com.whatsapp")
            
            task.contains("Silence", ignoreCase = true) -> 
                AndroidAction(ActionType.SET_SETTINGS, command = "DND_ON")
            
            else -> AndroidAction(ActionType.TRIGGER_INTENT, command = task)
        }
    }
}
