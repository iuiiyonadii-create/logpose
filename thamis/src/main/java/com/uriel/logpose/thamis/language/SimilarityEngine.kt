package com.uriel.logpose.thamis.language

import kotlin.math.sqrt

/**
 * Motor de similitud base para THAMIS.
 * Provee algoritmos puros de comparación de texto sin dependencias externas pesadas.
 */
object SimilarityEngine {

    /**
     * Punto de entrada estándar para similitud de texto.
     */
    fun score(a: String, b: String): Float {
        return calculate(a, b)
    }

    /**
     * Calcula una puntuación de similitud combinando Jaro-Winkler y Coseno.
     */
    fun calculate(a: String, b: String): Float {
        val normA = a.lowercase().trim()
        val normB = b.lowercase().trim()
        
        if (normA == normB) return 1f
        if (normA.isEmpty() || normB.isEmpty()) return 0f

        // 1. Similitud Jaro-Winkler (Estructural/Secuencial)
        val jaroWinklerScore = jaroWinkler(normA, normB)

        // 2. Similitud de Coseno (Bolsa de palabras/Frecuencia)
        val wordsA = normA.split(Regex("\\s+")).filter { it.length > 1 }
        val wordsB = normB.split(Regex("\\s+")).filter { it.length > 1 }
        
        val cosineScore = if (wordsA.isNotEmpty() && wordsB.isNotEmpty()) {
            cosineSimilarity(wordsA, wordsB)
        } else 0f

        // 3. Resultado combinado (50/50 para base)
        var finalScore = (jaroWinklerScore * 0.5f) + (cosineScore * 0.5f)

        // --- Ajustes de Confianza ---
        if (wordsA.isNotEmpty() && wordsB.isNotEmpty() && wordsA.first() == wordsB.first()) {
            finalScore += 0.1f
        }

        val lenDiff = Math.abs(wordsA.size - wordsB.size)
        if (lenDiff > 2) {
            finalScore -= 0.1f
        }

        return finalScore.coerceIn(0.0f, 1.0f)
    }

    /**
     * Implementación pura de Jaro-Winkler sin librerías externas.
     */
    fun jaroWinkler(s1: String, s2: String): Float {
        if (s1 == s2) return 1.0f
        if (s1.isEmpty() || s2.isEmpty()) return 0.0f

        val matchDistance = maxOf(s1.length, s2.length) / 2 - 1
        val s1Matches = BooleanArray(s1.length)
        val s2Matches = BooleanArray(s2.length)

        var matches = 0
        for (i in s1.indices) {
            val start = maxOf(0, i - matchDistance)
            val end = minOf(i + matchDistance + 1, s2.length)
            for (j in start until end) {
                if (s2Matches[j]) continue
                if (s1[i] != s2[j]) continue
                s1Matches[i] = true
                s2Matches[j] = true
                matches++
                break
            }
        }

        if (matches == 0) return 0.0f

        var transpositions = 0
        var k = 0
        for (i in s1.indices) {
            if (!s1Matches[i]) continue
            while (!s2Matches[k]) k++
            if (s1[i] != s2[k]) transpositions++
            k++
        }

        val jaro = (matches.toFloat() / s1.length + matches.toFloat() / s2.length + (matches - transpositions / 2f) / matches) / 3f
        
        // Winkler prefix scaling
        var prefix = 0
        for (i in 0 until minOf(4, minOf(s1.length, s2.length))) {
            if (s1[i] == s2[i]) prefix++ else break
        }

        return jaro + (prefix * 0.1f * (1.0f - jaro))
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
