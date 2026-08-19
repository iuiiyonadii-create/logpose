package com.uriel.logpose.thamis.knowledge.calls

import com.uriel.logpose.core.compat.core.AppContextProvider
import com.uriel.logpose.core.parser.PhoneticDictionary
import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.thamis.knowledge.KnowledgeRule

object CallKnowledge {

    private val dictionary: PhoneticDictionary by lazy {
        PhoneticDictionary(AppContextProvider.applicationContext)
    }

    val rules: List<KnowledgeRule> get() = listOf(
        KnowledgeRule(
            intent = Intent.CALL_CONTACT,
            phrases = dictionary.listaDe("verbos.sistema").filter { it.contains("llam") }.toSet() + setOf("llamar a", "llamá a")
        ),
        KnowledgeRule(
            intent = Intent.ANSWER_CALL,
            phrases = setOf("atender", "atende", "atendé", "responder", "contestar", "contestá", "aceptar")
        ),
        KnowledgeRule(
            intent = Intent.REJECT_CALL,
            phrases = setOf("cortar", "finalizar", "terminar", "rechazar", "rechazá", "no atender", "cancelar")
        )
    )

}
