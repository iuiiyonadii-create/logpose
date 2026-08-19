package com.uriel.logpose.thamis.knowledge.maps

import com.uriel.logpose.core.compat.core.AppContextProvider
import com.uriel.logpose.core.parser.PhoneticDictionary
import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.thamis.knowledge.KnowledgeRule

object NavigationKnowledge {

    private val dictionary: PhoneticDictionary by lazy {
        PhoneticDictionary(AppContextProvider.applicationContext)
    }

    val rules: List<KnowledgeRule> get() = listOf(
        KnowledgeRule(
            intent = Intent.NAVIGATE,
            phrases = (dictionary.listaDe("verbos.navegar") + dictionary.listaDe("navegacion.destinos_comunes") + dictionary.listaDe("navegacion.arterias") + dictionary.listaDe("navegacion.favoritos")).filter { it.isNotBlank() }.toSet()
        ),
        KnowledgeRule(
            intent = Intent.STOP_NAVIGATION,
            phrases = setOf(
                "parar navegación", "detener navegación", "cancelar ruta", "parar gps", "salir de mapas",
                "parar navegacion", "detener navegacion", "cancelar navegacion", "basta de gps"
            )
        )
    )

}
