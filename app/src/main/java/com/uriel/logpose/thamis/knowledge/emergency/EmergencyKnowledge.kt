package com.uriel.logpose.thamis.knowledge.emergency

import com.uriel.logpose.thamis.intent.Intent
import com.uriel.logpose.thamis.knowledge.KnowledgeRule

object EmergencyKnowledge {

    val rules = listOf(
        KnowledgeRule(
            intent = Intent.EMERGENCY,
            phrases = setOf(
                "emergencia",
                "auxilio",
                "ayuda",
                "llamar a la policia",
                "llamar a una ambulancia",
                "me accidente",
                "tuve un accidente",
                "necesito ayuda"
            )
        )
    )

}