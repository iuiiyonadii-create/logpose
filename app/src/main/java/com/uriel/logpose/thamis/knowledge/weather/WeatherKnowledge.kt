package com.uriel.logpose.thamis.knowledge.weather

import com.uriel.logpose.thamis.intent.Intent
import com.uriel.logpose.thamis.knowledge.KnowledgeRule

object WeatherKnowledge {

    val rules = listOf(
        KnowledgeRule(
            intent = Intent.WEATHER,
            phrases = setOf(
                "clima",
                "hace frio",
                "hace calor",
                "va a llover",
                "pronostico",
                "como esta el tiempo"
            )
        )
    )

}