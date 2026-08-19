package com.uriel.logpose.thamis.knowledge.music

import com.uriel.logpose.core.compat.core.AppContextProvider
import com.uriel.logpose.core.parser.PhoneticDictionary
import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.thamis.knowledge.KnowledgeRule

object MusicKnowledge {

    private val dictionary: PhoneticDictionary by lazy {
        PhoneticDictionary(AppContextProvider.applicationContext)
    }

    val rules: List<KnowledgeRule> get() = listOf(
        KnowledgeRule(
            intent = Intent.PLAY_MUSIC,
            phrases = (dictionary.listaDe("verbos.reproducir") + setOf("reproducción", "musica", "reproducime", "sonar")).toSet()
        ),
        KnowledgeRule(
            intent = Intent.PAUSE_MUSIC,
            phrases = (dictionary.listaDe("verbos.pausar") + setOf("pausa", "parar musica", "detener musica", "frenar musica")).toSet()
        ),
        KnowledgeRule(
            intent = Intent.REPEAT_MUSIC,
            phrases = dictionary.listaDe("verbos.bucle").toSet()
        )
    )

}
