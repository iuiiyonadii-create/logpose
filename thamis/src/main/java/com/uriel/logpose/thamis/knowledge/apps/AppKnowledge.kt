package com.uriel.logpose.thamis.knowledge.apps

import com.uriel.logpose.core.compat.core.AppContextProvider
import com.uriel.logpose.core.parser.PhoneticDictionary
import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.thamis.knowledge.KnowledgeRule

object AppKnowledge {

    private val dictionary: PhoneticDictionary by lazy {
        PhoneticDictionary(AppContextProvider.applicationContext)
    }

    val rules: List<KnowledgeRule> get() = listOf(
        KnowledgeRule(
            intent = Intent.OPEN_APP,
            phrases = (dictionary.listaDe("verbos.abrir") + dictionary.listaDe("apps")).toSet()
        ),
        KnowledgeRule(
            intent = Intent.SWITCH_TAB,
            phrases = setOf(
                "cambiar pestaña",
                "cambiar de pestaña",
                "siguiente pestaña",
                "pestaña siguiente",
                "otra pestaña",
                "cambia de pestaña",
                "cambiá de pestaña",
                "abrir pestaña",
                "abrí pestaña",
                "abri pestaña",
                "la pestaña de",
                "pestaña de"
            )
        )
    )

}
