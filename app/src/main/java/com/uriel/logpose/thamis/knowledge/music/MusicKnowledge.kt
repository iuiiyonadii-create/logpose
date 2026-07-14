package com.uriel.logpose.thamis.knowledge.music

import com.uriel.logpose.thamis.intent.Intent
import com.uriel.logpose.thamis.knowledge.KnowledgeRule

object MusicKnowledge {

    val rules = listOf(

        KnowledgeRule(

            intent = Intent.PLAY_MUSIC,

            phrases = setOf(

                "pone musica",

                "reproduci musica",

                "play",

                "inicia musica"

            )

        ),

        KnowledgeRule(

            intent = Intent.PAUSE_MUSIC,

            phrases = setOf(

                "pausa",

                "pausar",

                "detene musica"

            )

        )

    )

}