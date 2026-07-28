package com.uriel.logpose.thamis.knowledge.music

import com.uriel.logpose.thamis.intent.Intent
import com.uriel.logpose.thamis.knowledge.KnowledgeRule

object MusicKnowledge {

    val rules = listOf(
        KnowledgeRule(
            intent = Intent.PLAY_MUSIC,
            phrases = setOf(
                "pone musica", "poné música", "reproduci musica", "reproducir musica",
                "play", "inicia musica", "mandale musica", "ponete algo",
                "pone cumbia", "pone rock", "pone uzbekistan", "pone cuarteto",
                "reproduci", "reproducir", "un tema de", "pone un tema de",
                "poné un tema de", "escuchar a", "reproducir duki", "pone duki",
                "pone spotify", "poné spotify", "abrir spotify", "reproducí spotify",
                "pone musica en spotify", "poné música en spotify", "quería escuchar",
                "pone algo de", "poné algo de", "reproducí algo de", "reproducime",
                "poneme", "buscame", "buscar", "escuchar", "escucha", "sonar", "hace sonar"
            )
        ),
        KnowledgeRule(
            intent = Intent.PAUSE_MUSIC,
            phrases = setOf("pausa", "pausar", "detene musica", "parar musica", "silencio", "para", "parar", "detener", "shh", "callate", "cállate")
        ),
        KnowledgeRule(
            intent = Intent.REPEAT_MUSIC,
            phrases = setOf("repetir", "bucle", "en bucle", "repetir este tema", "pone en bucle", "poné en bucle", "otra vez", "de nuevo", "loop")
        )
    )

}
