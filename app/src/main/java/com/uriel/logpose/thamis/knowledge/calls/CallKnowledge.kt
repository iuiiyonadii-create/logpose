package com.uriel.logpose.thamis.knowledge.calls

import com.uriel.logpose.thamis.intent.Intent
import com.uriel.logpose.thamis.knowledge.KnowledgeRule

object CallKnowledge {

    val rules = listOf(
        KnowledgeRule(
            intent = Intent.CALL_CONTACT,
            phrases = setOf(
                "llamar",
                "llama a",
                "llamar a",
                "llámame a",
                "llamá a",
                "pegale un tubazo a",
                "comunicame con"
            )
        ),
        KnowledgeRule(
            intent = Intent.ANSWER_CALL,
            phrases = setOf(
                "atender",
                "atende",
                "atendé",
                "responder",
                "contestar",
                "contestá",
                "aceptar"
            )
        ),
        KnowledgeRule(
            intent = Intent.REJECT_CALL,
            phrases = setOf(
                "cortar",
                "finalizar",
                "terminar",
                "rechazar",
                "rechazá",
                "no atender",
                "cancelar"
            )
        )
    )

}