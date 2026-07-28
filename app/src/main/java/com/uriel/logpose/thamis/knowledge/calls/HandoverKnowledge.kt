package com.uriel.logpose.thamis.knowledge.calls

import com.uriel.logpose.thamis.intent.Intent
import com.uriel.logpose.thamis.knowledge.KnowledgeRule

object HandoverKnowledge {
    val rules = listOf(
        KnowledgeRule(
            intent = Intent.YIELD_CONTROL,
            phrases = setOf(
                "quiero hablar",
                "soltá el micro",
                "dame el micrófono",
                "hacer silencio",
                "modo silencio",
                "no me escuches",
                "pará un poco",
                "un segundo",
                "un minuto",
                "dejame hablar",
                "silencio logpose"
            )
        )
    )
}
