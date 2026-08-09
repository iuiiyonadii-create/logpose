package com.uriel.logpose.thamis.knowledge.music

import com.uriel.logpose.core.app.LogPoseApplication
import com.uriel.logpose.core.parser.PhoneticDictionary
import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.thamis.knowledge.KnowledgeRule

object VolumeKnowledge {

    private val dictionary: PhoneticDictionary by lazy {
        PhoneticDictionary(LogPoseApplication.instance)
    }

    val rules: List<KnowledgeRule> get() = listOf(
        KnowledgeRule(
            intent = Intent.SET_VOLUME,
            phrases = (
                dictionary.listaDe("verbos.volumen_subir") +
                dictionary.listaDe("verbos.volumen_bajar") +
                dictionary.listaDe("verbos.mute") +
                setOf(
                    "subi el volumen", "subir volumen", "subí", "baja el volumen", "bajar volumen", "bajá",
                    "bajalo", "subilo", "mas fuerte", "mas bajo", "dale volumen", "volumen arriba", "volumen abajo",
                    "pone volumen", "poné volumen", "sube", "baja", "gritar", "despacio", "fuerte", "mas alto", "más alto",
                    "mas bajito", "más bajito", "volumen", "volumen máximo", "volumen maximo", "a todo lo que da"
                )
            ).filter { it.isNotBlank() }.toSet()
        ),
        KnowledgeRule(
            intent = Intent.NEXT_TRACK,
            phrases = setOf("siguiente", "pasa la cancion", "pasá", "saltá", "otra", "el que sigue", "proximo", "próximo", "adelantar", "adelanta", "next")
        ),
        KnowledgeRule(
            intent = Intent.PREVIOUS_TRACK,
            phrases = setOf("anterior", "atras", "atrás", "la de antes", "el de antes", "volver", "volvé", "retroceder", "retrocede", "prev")
        )
    )
}
