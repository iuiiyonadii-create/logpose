package com.uriel.logpose.thamis.hypothesis

import com.uriel.logpose.thamis.decision.Evidence
import com.thamis.lab.core.contracts.intent.Intent

/**
 * Encargado de asignar puntajes a las hipótesis basadas en evidencias.
 */
object HypothesisScorer {

    fun score(intent: Intent, text: String, context: Map<String, String>): Pair<Float, List<Evidence>> {
        val evidences = mutableListOf<Evidence>()
        var totalScore = 0f

        // 1. Evaluación de Triggers Gramaticales
        if (intent == Intent.PLAY_MUSIC) {
            val triggers = listOf("pone", "poneme", "reproducir", "mandale")
            if (triggers.any { text.contains(it) }) {
                val e = Evidence(Evidence.Type.GRAMMAR_MATCH, 0.4f, "Match de disparador musical")
                evidences.add(e)
                totalScore += e.weight
            }
        }

        // 2. Evaluación Contextual (Boost)
        if (context["isMusicPlaying"] == "true") {
            if (intent == Intent.NEXT_TRACK || intent == Intent.PAUSE_MUSIC) {
                val e = Evidence(Evidence.Type.CONTEXT_BOOST, 0.3f, "Boost por reproducción activa")
                evidences.add(e)
                totalScore += e.weight
            }
        }

        return totalScore.coerceAtMost(1.0f) to evidences
    }
}
