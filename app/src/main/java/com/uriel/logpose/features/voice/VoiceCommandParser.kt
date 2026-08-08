package com.uriel.logpose.features.voice

import com.thamis.lab.core.contracts.command.LogPoseCommand

/**
 * FASE 26.2 — LOGPOSE MVP CORE
 * FASE 5: VOICE COMMAND PARSER
 */
object VoiceCommandParser {

    private const val TRIGGER = "logpose"

    /**
     * Parsea un texto crudo para identificar un comando básico.
     */
    fun parse(text: String): LogPoseCommand {
        val cleanText = text.lowercase().trim()
        
        if (!cleanText.contains(TRIGGER)) return LogPoseCommand.Unknown

        return when {
            cleanText.contains("reproducir") || cleanText.contains("play") -> LogPoseCommand.PlayMusic("")
            cleanText.contains("pausa") || cleanText.contains("parar") -> LogPoseCommand.PauseMusic
            cleanText.contains("siguiente") -> LogPoseCommand.NextTrack
            cleanText.contains("subir volumen") -> LogPoseCommand.VolumeUp
            cleanText.contains("bajar volumen") -> LogPoseCommand.VolumeDown
            cleanText.contains("llamar") -> LogPoseCommand.Call("")
            cleanText.contains("detener") -> LogPoseCommand.StopListening
            cleanText.contains("notificaciones") -> LogPoseCommand.ReadNotifications
            else -> LogPoseCommand.Unknown
        }
    }
}
