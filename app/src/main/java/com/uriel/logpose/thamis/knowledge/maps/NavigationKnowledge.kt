package com.uriel.logpose.thamis.knowledge.maps

import com.uriel.logpose.core.app.LogPoseApplication
import com.uriel.logpose.core.parser.PhoneticDictionary
import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.thamis.knowledge.KnowledgeRule

object NavigationKnowledge {

    private val dictionary: PhoneticDictionary by lazy {
        PhoneticDictionary(LogPoseApplication.instance)
    }

    val rules: List<KnowledgeRule> get() = listOf(
        KnowledgeRule(
            intent = Intent.NAVIGATE,
            phrases = (dictionary.listaDe("verbos.navegar") + dictionary.listaDe("navegacion.destinos_comunes")).toSet()
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
