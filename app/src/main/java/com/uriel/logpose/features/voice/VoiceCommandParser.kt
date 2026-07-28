package com.uriel.logpose.features.voice

import com.uriel.logpose.core.Command

/**
 * FASE 26.2 — LOGPOSE MVP CORE
 * FASE 5: VOICE COMMAND PARSER
 */
object VoiceCommandParser {

    private const val TRIGGER = "logpose"

    /**
     * Parsea un texto crudo para identificar un comando básico.
     */
    fun parse(text: String): Command {
        val cleanText = text.lowercase().trim()
        
        if (!cleanText.contains(TRIGGER)) return Command.UNKNOWN

        return when {
            cleanText.contains("reproducir") || cleanText.contains("play") -> Command.PLAY_MUSIC
            cleanText.contains("pausa") || cleanText.contains("parar") -> Command.PAUSE_MUSIC
            cleanText.contains("siguiente") -> Command.NEXT_TRACK
            cleanText.contains("subir volumen") -> Command.INCREASE_VOLUME
            cleanText.contains("bajar volumen") -> Command.DECREASE_VOLUME
            cleanText.contains("llamar") -> Command.CALL_CONTACT
            cleanText.contains("detener") -> Command.STOP_LOGPOSE
            cleanText.contains("notificaciones") -> Command.READ_NOTIFICATIONS
            else -> Command.UNKNOWN
        }
    }
}
