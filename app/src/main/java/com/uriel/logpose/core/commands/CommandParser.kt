package com.uriel.logpose.core.commands

/**
 * Maps raw speech strings to structured Command objects.
 */
class CommandParser {

    fun parse(text: String): CommandType {
        val input = text.lowercase().trim()
        return when {
            input.contains("reproducir") || input.contains("play") -> CommandType.PLAY_MUSIC
            input.contains("pausa") || input.contains("pause") -> CommandType.PAUSE_MUSIC
            input.contains("siguiente") || input.contains("next") -> CommandType.NEXT_TRACK
            input.contains("anterior") || input.contains("previous") -> CommandType.PREVIOUS_TRACK
            input.contains("subir volumen") || input.contains("más fuerte") -> CommandType.VOLUME_UP
            input.contains("bajar volumen") || input.contains("más bajo") -> CommandType.VOLUME_DOWN
            input.contains("silenciar") || input.contains("mute") -> CommandType.MUTE
            input.contains("llamar") -> CommandType.CALL
            input.contains("contestar") || input.contains("atender") -> CommandType.ANSWER_CALL
            input.contains("rechazar") -> CommandType.REJECT_CALL
            input.contains("conducción") -> CommandType.DRIVING_MODE
            input.contains("estado") -> CommandType.GET_STATUS
            input.contains("volver") || input.contains("salir") -> CommandType.EXIT_PRIVACY
            else -> CommandType.UNKNOWN
        }
    }
}
