package com.uriel.logpose.thamis.hypothesis

import com.uriel.logpose.thamis.decision.Hypothesis
import com.uriel.logpose.thamis.intent.Intent
import com.uriel.logpose.thamis.intent.IntentDetector
import com.uriel.logpose.thamis.entity.EntityExtractor
import com.uriel.logpose.thamis.decision.Evidence
import com.uriel.logpose.thamis.cognitive.model.WorldState
import com.uriel.logpose.thamis.cognitive.disambiguation.EntityDisambiguationEngine

/**
 * Motor de Generación de Hipótesis v1.2.
 * Genera múltiples interpretaciones competitivas para un mismo texto para mejorar el entendimiento.
 */
object HypothesisEngine {

    fun generate(text: String, context: Map<String, String>, worldState: WorldState? = null): List<Pair<Hypothesis, List<Evidence>>> {
        val list = mutableListOf<Pair<Hypothesis, List<Evidence>>>()

        // 1. Detección Primaria (IntentDetector)
        val primaryDetection = IntentDetector.detect(text)
        if (primaryDetection.intent != Intent.UNKNOWN) {
            val h1 = buildHypothesis(primaryDetection.intent, text, context, worldState, "PrimaryDetector")
            list.add(h1)
        }

        // 2. Detección Secundaria (Basada en palabras clave si la primera falló o es débil)
        if (primaryDetection.score < 0.8f) {
            val alternativeIntents = findAlternativeIntents(text)
            for (altIntent in alternativeIntents) {
                if (altIntent != primaryDetection.intent) {
                    val hAlt = buildHypothesis(altIntent, text, context, worldState, "AlternativeScanner")
                    list.add(hAlt)
                }
            }
        }

        // 3. Hipótesis de Apertura Genérica (Siempre como fallback de bajo puntaje)
        if (list.none { it.first.intent == Intent.OPEN_APP }) {
             val hOpen = buildHypothesis(Intent.OPEN_APP, text, context, worldState, "GenericFallback")
             list.add(hOpen.copy(first = hOpen.first.copy(score = hOpen.first.score * 0.5f)))
        }

        return list.sortedByDescending { it.first.score }
    }

    private fun buildHypothesis(intent: Intent, text: String, context: Map<String, String>, worldState: WorldState?, source: String): Pair<Hypothesis, List<Evidence>> {
        val entities = EntityExtractor.extract(intent, text)
        
        if (worldState != null) {
            entities.forEach { (_, value) ->
                EntityDisambiguationEngine.disambiguate(value, worldState)
            }
        }
        
        val (score, evidences) = HypothesisScorer.score(intent, text, context)
        
        val hypothesis = Hypothesis(
            intent = intent,
            entities = entities,
            score = score,
            source = source
        )
        return hypothesis to evidences
    }

    private fun findAlternativeIntents(text: String): List<Intent> {
        val alts = mutableListOf<Intent>()
        val words = text.lowercase().split(" ")
        
        if (words.any { it in listOf("musica", "música", "cancion", "canción", "pone", "escuchar") }) alts.add(Intent.PLAY_MUSIC)
        if (words.any { it in listOf("llama", "llamar", "hablar") }) alts.add(Intent.CALL_CONTACT)
        if (words.any { it in listOf("ir", "navegar", "donde", "dónde", "como", "cómo") }) alts.add(Intent.NAVIGATE)
        
        return alts.distinct()
    }
}
