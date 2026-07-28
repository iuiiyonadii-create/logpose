package com.uriel.logpose.thamis.knowledge.maps

import com.uriel.logpose.thamis.intent.Intent
import com.uriel.logpose.thamis.knowledge.KnowledgeRule

object NavigationKnowledge {

    val rules = listOf(
        KnowledgeRule(
            intent = Intent.NAVIGATE,
            phrases = setOf(
                "ir a",
                "ir",
                "como vamos",
                "cómo vamos",
                "como llego a",
                "cómo llego a",
                "navegar a",
                "llevame a",
                "llévame a",
                "donde queda",
                "dónde queda",
                "ruta a",
                "guiame a",
                "poné el gps a",
                "anda a",
                "encara para",
                "rumbear para",
                "rumbo a",
                "quiero ir a",
                "a casa",
                "al laburo",
                "al trabajo",
                "ir casa",
                "ir laburo",
                "ir oficina",
                "guiame a casa",
                "llevame a casa",
                "encara al rancho",
                "casa",
                "trabajo",
                "laburo",
                "chamba",
                "buscá una gasolinera",
                "buscá nafta",
                "dónde hay un cajero",
                "buscá un estacionamiento",
                "buscá un mcdonalds",
                "dónde hay una gomeria",
                "buscá una gomeria",
                "tengo poca nafta"
            )
        ),
        KnowledgeRule(
            intent = Intent.STOP_NAVIGATION,
            phrases = setOf(
                "cancelar navegación",
                "dejá de navegar",
                "terminá la navegación",
                "ya llegué",
                "detener gps",
                "parar navegación",
                "cancelar viaje",
                "cancelo el viaje",
                "cancela el viaje",
                "terminar viaje",
                "cortar el gps",
                "sacá el mapa"
            )
        )
    )

}
