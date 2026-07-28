package com.uriel.logpose.features.voice_ai

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.features.voice.VoiceCommandParser

/**
 * FASE 26.3 — LOGPOSE VOICE INTELLIGENCE
 * FASE 1: VOICE ENGINE CORE
 */
object VoiceEngine {

    fun processSpeech(text: String) {
        LogPoseLogger.d("VoiceEngine: Procesando habla: $text")
        
        // Integración inicial con el parser de comandos
        val command = VoiceCommandParser.parse(text)
        
        if (command != com.uriel.logpose.core.Command.UNKNOWN) {
            LogPoseLogger.i("VoiceEngine: Comando detectado: $command")
            // Enviar al ActionManager (Fase 7)
        } else {
            LogPoseLogger.w("VoiceEngine: No se entendió el comando.")
        }
    }
}
