package com.uriel.logpose.thamis.knowledge.music

import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.thamis.knowledge.KnowledgeRule

object VolumeKnowledge {

    val rules = listOf(
        KnowledgeRule(
            intent = Intent.SET_VOLUME,
            phrases = setOf(
                "subi el volumen", "subir volumen", "subí", "baja el volumen", "bajar volumen", "bajá",
                "bajalo", "subilo", "mas fuerte", "mas bajo", "dale volumen", "volumen arriba", "volumen abajo",
                "pone volumen", "poné volumen", "sube", "baja", "gritar", "despacio", "fuerte", "mas alto", "más alto",
                "mas bajito", "más bajito", "volumen"
            )
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
