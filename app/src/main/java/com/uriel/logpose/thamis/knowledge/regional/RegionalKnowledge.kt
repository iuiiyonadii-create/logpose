package com.uriel.logpose.thamis.knowledge.regional

import com.uriel.logpose.core.app.LogPoseApplication
import com.uriel.logpose.core.parser.PhoneticDictionary
import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.thamis.knowledge.KnowledgeRule

/**
 * Conocimiento regional adaptado para Argentina (Misión #016).
 */
object RegionalKnowledge {

    private val dictionary: PhoneticDictionary by lazy {
        PhoneticDictionary(LogPoseApplication.instance)
    }

    val rules: List<KnowledgeRule> get() = listOf(
        KnowledgeRule(
            intent = Intent.SAFETY_ALERT,
            phrases = dictionary.listaDe("verbos.alerta_seguridad").toSet()
        ),
        KnowledgeRule(
            intent = Intent.TRAFFIC_STATUS,
            phrases = dictionary.listaDe("verbos.trafico").toSet()
        ),
        KnowledgeRule(
            intent = Intent.RESTAURANT_SEARCH,
            phrases = dictionary.listaDe("verbos.comida").toSet()
        ),
        KnowledgeRule(
            intent = Intent.TRANSPORT_INFO,
            phrases = dictionary.listaDe("verbos.transporte").toSet()
        ),
        KnowledgeRule(
            intent = Intent.VEHICLE_STATUS,
            phrases = dictionary.listaDe("verbos.moto_diagnostico").toSet()
        ),
        KnowledgeRule(
            intent = Intent.FUEL_LEVEL,
            phrases = dictionary.listaDe("verbos.moto_nafta").toSet()
        ),
        KnowledgeRule(
            intent = Intent.MAINTENANCE_INFO,
            phrases = dictionary.listaDe("verbos.moto_mantenimiento").toSet()
        ),
        KnowledgeRule(
            intent = Intent.ENGINE_TEMP,
            phrases = dictionary.listaDe("verbos.moto_temperatura").toSet()
        )
    )
}
