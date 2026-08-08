package com.uriel.logpose.thamis.language.advanced

import com.uriel.logpose.thamis.language.PhoneticEngine
import com.uriel.logpose.thamis.language.SimilarityEngine

/**
 * Motor de lenguaje avanzado de THAMIS (V7.1).
 * Optimizado: Soporte para comparación por llaves fonéticas pre-calculadas.
 */
object AdvancedLanguageEngine {

    /**
     * Calcula una puntuación de similitud híbrida.
     * Versión estándar para comparaciones rápidas.
     */
    fun getSimilarity(input: String, target: String): Float {
        val metaInput = SpanishMetaphoneRioplatense.generate(input)
        val metaTarget = SpanishMetaphoneRioplatense.generate(target)
        return getSimilarityWithMetas(input, metaInput, target, metaTarget)
    }

    /**
     * Calcula similitud utilizando metaphones ya calculados (Misión #011).
     * Soporta Capa de Probabilidad Semántica v4.0.
     */
    fun getSimilarityWithMetas(
        input: String, 
        metaInput: String, 
        target: String, 
        metaTarget: String,
        noiseLevel: Float = 0.0f,
        semanticCloud: Map<String, Float> = emptyMap()
    ): Float {
        if (input == target) return 1.0f
        
        // 1. Similitud Fonética Pro (ALF-R v4.0 con llaves pre-calculadas)
        val phoneticScore = PhoneticEngine.similarityWithKeys(
            metaInput, metaTarget, target, semanticCloud
        )

        // 2. Similitud Estructural (NGrams)
        val nGramScore = FuzzyNGramMatcher.score(input, target)

        // 3. Similitud Semántica Base (Jaro-Winkler + Cosine)
        val semanticBase = SimilarityEngine.calculate(input, target)

        // 4. Consolidación Final: Peso Fonético incrementado en v4.0
        val finalScore = (phoneticScore * 0.6f) + (semanticBase * 0.25f) + (nGramScore * 0.15f)

        return finalScore.coerceIn(0.0f, 1.0f)
    }
}
