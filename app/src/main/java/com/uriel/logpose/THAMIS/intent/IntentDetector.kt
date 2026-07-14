package com.uriel.logpose.THAMIS.intent

/**
 * Detecta la intención principal del usuario.
 *
 * THAMIS no depende de LogCore.
 * Toda la lógica de comprensión comienza aquí.
 */
object IntentDetector {

    fun detect(text: String): com.uriel.logpose.THAMIS.intent.Intent {

        val normalized = text
            .trim()
            .lowercase()

        return when {

            // Música
            normalized.contains("música") ||
                    normalized.contains("musica") ||
                    normalized.contains("reproducí") ||
                    normalized.contains("reproduci") ||
                    normalized.contains("poné") ||
                    normalized.contains("pone") ||
                    normalized.contains("play") ->
                _root_ide_package_.com.uriel.logpose.THAMIS.intent.Intent.PLAY_MUSIC

            normalized.contains("pausa") ||
                    normalized.contains("pausar") ->
                _root_ide_package_.com.uriel.logpose.THAMIS.intent.Intent.PAUSE_MUSIC

            normalized.contains("siguiente") ->
                _root_ide_package_.com.uriel.logpose.THAMIS.intent.Intent.NEXT_TRACK

            normalized.contains("anterior") ->
                _root_ide_package_.com.uriel.logpose.THAMIS.intent.Intent.PREVIOUS_TRACK

            else ->
                _root_ide_package_.com.uriel.logpose.THAMIS.intent.Intent.UNKNOWN
        }
    }

}