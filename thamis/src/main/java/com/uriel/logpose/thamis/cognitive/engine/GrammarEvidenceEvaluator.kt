package com.uriel.logpose.thamis.cognitive.engine

import com.uriel.logpose.thamis.cognitive.model.Evidence
import com.uriel.logpose.thamis.cognitive.model.Hypothesis
import com.uriel.logpose.thamis.cognitive.model.WorldState
import com.uriel.logpose.thamis.cognitive.model.Goal

/**
 * Evalúa coincidencias de verbos y estructuras gramaticales.
 * Hardened v1.2: Detecta verbos ancla y penaliza oraciones sin sentido estructural.
 */
class GrammarEvidenceEvaluator : EvidenceEvaluator {

    private val ANCHOR_VERBS = mapOf(
        Goal.Category.MULTIMEDIA to listOf("pone", "poné", "reproducí", "reproducir", "escuchar", "escuchá"),
        Goal.Category.NAVIGATION to listOf("ir", "anda", "andá", "llevame", "lleváme", "navegar", "navegá"),
        Goal.Category.COMMUNICATION to listOf("llama", "llamá", "llamar", "llamame", "llamáme", "mandale", "escribile")
    )

    override fun evaluate(hypothesis: Hypothesis, worldState: WorldState): List<Evidence> {
        val evidences = mutableListOf<Evidence>()
        val category = hypothesis.candidateGoal.category
        val text = hypothesis.candidateGoal.parameters["raw_text"]?.lowercase() ?: ""

        // 1. Verificar Verbos Ancla (+0.4)
        val anchors = ANCHOR_VERBS[category] ?: emptyList()
        if (anchors.any { text.startsWith(it) }) {
            evidences.add(Evidence(
                type = Evidence.Type.POSITIVE,
                source = Evidence.Source.GRAMMAR,
                impact = 0.4f,
                description = "Verbo ancla detectado al inicio",
                expirationMs = 5000L
            ))
        }

        // 2. Penalizar si la oración es demasiado corta para la categoría (-0.3)
        if (text.split(" ").size < 2 && category != Goal.Category.SYSTEM_NEED) {
            evidences.add(Evidence(
                type = Evidence.Type.NEGATIVE,
                source = Evidence.Source.GRAMMAR,
                impact = -0.3f,
                description = "Oración demasiado corta para interpretación segura",
                expirationMs = 2000L
            ))
        }

        return evidences
    }
}
