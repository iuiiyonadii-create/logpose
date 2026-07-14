package com.uriel.logpose.logcore.core.intent

object IntentPatterns {

    val patterns = listOf(

        IntentPattern(
            category = IntentCategory.MUSIC,
            intent = Intent.PLAY_MUSIC,
            phrases = listOf(
                "reproducir música",
                "poner música",
                "poné música",
                "play",
                "iniciar música"
            )
        ),

        IntentPattern(
            category = IntentCategory.MUSIC,
            intent = Intent.PAUSE_MUSIC,
            phrases = listOf(
                "pausa",
                "pausar",
                "detener música"
            )
        ),

        IntentPattern(
            category = IntentCategory.MUSIC,
            intent = Intent.NEXT_TRACK,
            phrases = listOf(
                "siguiente",
                "siguiente canción"
            )
        ),

        IntentPattern(
            category = IntentCategory.MUSIC,
            intent = Intent.PREVIOUS_TRACK,
            phrases = listOf(
                "anterior",
                "canción anterior"
            )
        )
    )
}