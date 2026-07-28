package com.uriel.logpose.thamis.language.advanced

import com.uriel.logpose.thamis.language.PhoneticEngine
import com.uriel.logpose.thamis.language.SimilarityEngine

/**
 * Motor de lenguaje avanzado de THAMIS.
 * Combina Metaphone Rioplatense y Trigramas para máxima precisión en moto.
 */
object AdvancedLanguageEngine {

    /**
     * Calcula una puntuación de similitud híbrida.
     * Combina Similitud Fonética Pro (Metaphone) y Similitud Estructural (NGrams).
     */
    fun getSimilarity(input: String, target: String): Float {
        // 1. Similitud Fonética Pro (Metaphone Rioplatense)
        val metaInput = SpanishMetaphoneRioplatense.generate(input)
        val metaTarget = SpanishMetaphoneRioplatense.generate(target)
        
        // Usamos Jaro-Winkler sobre las claves Metaphone
        val phoneticProScore = SimilarityEngine.score(metaInput, metaTarget)

        // 2. Similitud por N-Gramas (Resistencia al ruido)
        val nGramScore = FuzzyNGramMatcher.score(input, target)

        // 3. Resultado combinado (70% Fonética / 30% N-Gramas)
        val finalScore = (phoneticProScore * 0.7f) + (nGramScore * 0.3f)

        return finalScore.coerceIn(0.0f, 1.0f)
    }
}
