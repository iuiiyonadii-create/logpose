package com.uriel.logpose.thamis.decision

import com.uriel.logpose.thamis.knowledge.KnowledgeBase
import com.uriel.logpose.thamis.language.SimilarityEngine
import com.uriel.logpose.thamis.normalizer.LanguageNormalizer

/**
 * Genera las posibles interpretaciones de una solicitud.
 */
object HypothesisEngine {

    fun generate(text: String): List<Hypothesis> {

        val normalized = LanguageNormalizer.normalize(text)

        val hypotheses = mutableListOf<Hypothesis>()

        for (entry in KnowledgeBase.entries) {

            var bestScore = 0f

            for (phrase in entry.phrases) {

                val score = SimilarityEngine.score(
                    normalized,
                    phrase
                )

                if (score > bestScore) {
                    bestScore = score
                }
            }

            if (bestScore >= 0.60f) {

                hypotheses.add(
                    Hypothesis(
                        intent = entry.intent,
                        score = bestScore,
                        evidence = TODO()
                    )
                )
            }
        }

        return hypotheses.sortedByDescending { it.score }
    }
}