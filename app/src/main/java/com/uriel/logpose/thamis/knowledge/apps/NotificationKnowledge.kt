package com.uriel.logpose.thamis.knowledge.apps

import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.thamis.knowledge.KnowledgeRule

object NotificationKnowledge {

    val rules = listOf(
        KnowledgeRule(
            intent = Intent.READ_NOTIFICATION,
            phrases = setOf(
                "leer",
                "leé",
                "leeme",
                "escuchar",
                "¿qué dice?",
                "qué dice",
                "contame",
                "decime",
                "leé los mensajes",
                "leeme los mensajes",
                "hay mensajes",
                "tenés mensajes",
                "qué dice el whatsapp",
                "quién escribió",
                "leé el último",
                "notificaciones",
                "leé las notificaciones",
                "quien hablo",
                "quién habló",
                "quien mando mensaje",
                "quién mandó mensaje",
                "que pasa",
                "qué pasa",
                "noticias",
                "novedades",
                "novas"
            )
        ),
        KnowledgeRule(
            intent = Intent.SEND_MESSAGE,
            phrases = setOf(
                "responder",
                "contestá",
                "mandar mensaje",
                "enviar mensaje",
                "escribile a",
                "mandale un whatsapp a",
                "decile a",
                "mensaje a",
                "mandá",
                "manda",
                "mandame",
                "escribile",
                "mandale",
                "mandale un mensaje a",
                "mandale mensaje a",
                "avisale a",
                "avisale que",
                "mandale que"
            )
        )
    )

}
