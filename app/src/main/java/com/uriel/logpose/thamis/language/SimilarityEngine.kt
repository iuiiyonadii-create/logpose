package com.uriel.logpose.thamis.language

import com.uriel.logpose.thamis.ThamisConfiguration
import com.uriel.logpose.thamis.language.advanced.AdvancedLanguageEngine
import org.apache.commons.text.similarity.JaroWinklerSimilarity
import kotlin.math.sqrt

/**
 * Motor de similitud avanzado para THAMIS.
 * Utiliza algoritmos de la industria (Jaro-Winkler) y Coseno para máxima comprensión.
 */
object SimilarityEngine {

    private val jaroWinkler = JaroWinklerSimilarity()

    fun score(a: String, b: String): Float {
        // --- REDIRECCIÓN PRO (Fase 16) ---
        if (ThamisConfiguration.useAdvancedLanguageEngine) {
            return AdvancedLanguageEngine.getSimilarity(a, b)
        }

        val normA = a.lowercase().trim()
        val normB = b.lowercase().trim()
        
        if (normA == normB) return 1f
        if (normA.isEmpty() || normB.isEmpty()) return 0f

        // 1. Similitud Jaro-Winkler sobre Llaves Fonéticas (60%)
        val keyA = PhoneticEngine.getPhoneticKey(normA)
        val keyB = PhoneticEngine.getPhoneticKey(normB)
        
        val jaroWinklerScore = jaroWinkler.apply(keyA, keyB).toFloat()

        // 2. Similitud de Coseno para estructura (40%)
        val wordsA = normA.split(Regex("\\s+")).filter { it.length > 1 }
        val wordsB = normB.split(Regex("\\s+")).filter { it.length > 1 }
        
        val cosineScore = if (wordsA.isNotEmpty() && wordsB.isNotEmpty()) {
            cosineSimilarity(wordsA, wordsB)
        } else 0f

        // 3. Resultado base
        var finalScore = (jaroWinklerScore * 0.6f) + (cosineScore * 0.4f)

        // --- OPTIMIZACIÓN DE ENTENDIMIENTO ---
        
        // Bonus por coincidencia de primer verbo (Fundamental en comandos)
        if (wordsA.isNotEmpty() && wordsB.isNotEmpty() && wordsA.first() == wordsB.first()) {
            finalScore += 0.15f
        }

        // Penalización por diferencia excesiva de longitud
        val lenDiff = Math.abs(wordsA.size - wordsB.size)
        if (lenDiff > 2) {
            finalScore -= 0.1f
        }

        // Bonus por Jaccard (Intersección de palabras)
        val intersectCount = wordsA.intersect(wordsB).size
        val jaccard = intersectCount.toFloat() / (wordsA.size + wordsB.size - intersectCount).toFloat()
        if (jaccard > 0.5f) {
            finalScore += 0.05f
        }

        return finalScore.coerceIn(0.0f, 1.0f)
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
