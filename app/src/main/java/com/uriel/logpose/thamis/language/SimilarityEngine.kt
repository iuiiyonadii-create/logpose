package com.uriel.logpose.thamis.language

import org.apache.commons.text.similarity.JaroWinklerSimilarity
import kotlin.math.sqrt

/**
 * Motor de similitud base para THAMIS.
 * Provee algoritmos puros de comparación de texto sin lógica de negocio ni recursión.
 */
object SimilarityEngine {

    private val jaroWinkler = JaroWinklerSimilarity()

    /**
     * Punto de entrada estándar para similitud de texto.
     */
    fun score(a: String, b: String): Float {
        return calculate(a, b)
    }

    /**
     * Calcula una puntuación de similitud combinando Jaro-Winkler y Coseno.
     * @param a Texto A (normalizado)
     * @param b Texto B (normalizado)
     * @return Puntuación entre 0.0 y 1.0
     */
    fun calculate(a: String, b: String): Float {
        val normA = a.lowercase().trim()
        val normB = b.lowercase().trim()
        
        if (normA == normB) return 1f
        if (normA.isEmpty() || normB.isEmpty()) return 0f

        // 1. Similitud Jaro-Winkler (Estructural/Secuencial)
        val jaroWinklerScore = jaroWinkler.apply(normA, normB).toFloat()

        // 2. Similitud de Coseno (Bolsa de palabras/Frecuencia)
        val wordsA = normA.split(Regex("\\s+")).filter { it.length > 1 }
        val wordsB = normB.split(Regex("\\s+")).filter { it.length > 1 }
        
        val cosineScore = if (wordsA.isNotEmpty() && wordsB.isNotEmpty()) {
            cosineSimilarity(wordsA, wordsB)
        } else 0f

        // 3. Resultado combinado (50/50 para base)
        var finalScore = (jaroWinklerScore * 0.5f) + (cosineScore * 0.5f)

        // --- Ajustes de Confianza ---
        
        // Bonus por coincidencia de primer término (Acción/Verbo)
        if (wordsA.isNotEmpty() && wordsB.isNotEmpty() && wordsA.first() == wordsB.first()) {
            finalScore += 0.1f
        }

        // Penalización por disparidad de longitud
        val lenDiff = Math.abs(wordsA.size - wordsB.size)
        if (lenDiff > 2) {
            finalScore -= 0.1f
        }

        return finalScore.coerceIn(0.0f, 1.0f)
    }

    /**
     * Expone Jaro-Winkler puro para otros motores.
     */
    fun jaroWinkler(a: String, b: String): Float {
        if (a.isEmpty() || b.isEmpty()) return 0f
        return jaroWinkler.apply(a, b).toFloat()
    }

    private fun cosineSimilarity(listA: List<String>, listB: List<String>): Float {
        val setA = listA.toSet()
        val setB = listB.toSet()
        val allWords = setA + setB

        var dotProduct = 0f
        var normA = 0f
        var normB = 0f
        
        for (word in allWords) {
            val vA = if (setA.contains(word)) 1f else 0f
            val vB = if (setB.contains(word)) 1f else 0f
            
            dotProduct += vA * vB
            normA += vA * vA
            normB += vB * vB
        }

        if (normA == 0f || normB == 0f) return 0f
        return (dotProduct / (sqrt(normA.toDouble()) * sqrt(normB.toDouble()))).toFloat()
    }
}
