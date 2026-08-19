package com.uriel.logpose.thamis.knowledge.legal

import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.thamis.knowledge.KnowledgeRule

/**
 * Conocimiento sobre legalidad, seguridad y cumplimiento de la Store.
 */
object LegalKnowledge {

    val rules = listOf(
        KnowledgeRule(
            intent = Intent.ASK_LEGAL,
            phrases = setOf(
                "es legal esto",
                "es legal lo que estamos haciendo",
                "tenemos problemas legales",
                "es legal usar spotify así",
                "puedo tener problemas por esta app",
                "estas segura",
                "estás segura",
                "segura de que es legal"
            )
        ),
        KnowledgeRule(
            intent = Intent.ASK_PLAY_STORE,
            phrases = setOf(
                "puedo subirlo a la store",
                "puedo subir la app a google play",
                "me van a bloquear en la store",
                "pasa la revision de google",
                "es seguro subirlo a la store",
                "google me va a banear",
                "esta bien para la store",
                "está bien para la store"
            )
        )
    )
}
