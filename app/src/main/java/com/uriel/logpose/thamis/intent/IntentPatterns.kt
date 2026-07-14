package com.uriel.logpose.thamis.intent

/**
 * Base de conocimiento de THAMIS.
 */
object IntentPatterns {

    val all = listOf(

        IntentPattern(

            intent = Intent.PLAY_MUSIC,

            phrases = setOf(
                "musica",
                "pone musica",
                "pone musica por favor",
                "reproduci musica",
                "play",
                "inicia musica"
            )

        ),

        IntentPattern(

            intent = Intent.PAUSE_MUSIC,

            phrases = setOf(
                "pausa",
                "pausar",
                "detene musica"
            )

        ),

        IntentPattern(

            intent = Intent.NEXT_TRACK,

            phrases = setOf(
                "siguiente",
                "siguiente cancion"
            )

        ),

        IntentPattern(

            intent = Intent.PREVIOUS_TRACK,

            phrases = setOf(
                "anterior",
                "cancion anterior"
            )

        )

    )

}