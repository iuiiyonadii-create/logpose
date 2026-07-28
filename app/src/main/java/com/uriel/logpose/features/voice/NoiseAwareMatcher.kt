package com.uriel.logpose.features.voice

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * NoiseAwareMatcher: El cerebro probabilístico de Thamis.
 * Combina el medidor de ruido de Kimi con el peso de sibilancias de Claude.
 */
object NoiseAwareMatcher {

    private val SIBILANT_CHARS = setOf('s', 'z', 'x', 'c', 'f', 'j')
    private const val SIBILANT_COST = 0.4
    private const val REGULAR_COST = 1.0

    data class MatchResult(
        val bestMatch: String,
        val confidence: Float,
        val originalText: String
    )

    fun findBestMatch(
        text: String,
        vocabulary: List<String>,
        voskConfidence: Float,
        noiseLevel: Float
    ): MatchResult? {
        if (text.isBlank()) return null
        
        // Calculamos el umbral dinámico (Claude's logic)
        // A más ruido y menos confianza, bajamos el requerimiento
        val threshold = (0.50f + (noiseLevel * 0.20f) - (voskConfidence * 0.10f)).coerceIn(0.40f, 0.75f)
        
        var bestCandidate = ""
        var highestScore = 0f

        for (candidate in vocabulary) {
            val score = weightedSimilarity(text.lowercase(), candidate.lowercase())
            if (score > highestScore) {
                highestScore = score
                bestCandidate = candidate
            }
        }

        return if (highestScore >= threshold) {
            LogPoseLogger.d("NoiseAware: Match found -> $bestCandidate (Score: $highestScore, Threshold: $threshold)")
            MatchResult(bestCandidate, highestScore, text)
        } else {
            LogPoseLogger.w("NoiseAware: No match over threshold. Best was $bestCandidate ($highestScore < $threshold)")
            null
        }
    }

    /**
     * Similitud ponderada: las fallas en sibilancias (viento) cuestan menos.
     */
    private fun weightedSimilarity(s1: String, s2: String): Float {
        if (s1 == s2) return 1.0f
        val dist = weightedLevenshtein(s1, s2)
        val maxLen = maxOf(s1.length, s2.length)
        return (1.0 - (dist / maxLen)).toFloat().coerceIn(0f, 1f)
    }

    private fun weightedLevenshtein(a: String, b: String): Double {
        val dp = Array(a.length + 1) { DoubleArray(b.length + 1) }
        for (i in 0..a.length) dp[i][0] = i.toDouble()
        for (j in 0..b.length) dp[0][j] = j.toDouble()

        for (i in 1..a.length) {
            for (j in 1..b.length) {
                if (a[i - 1] == b[j - 1]) {
                    dp[i][j] = dp[i - 1][j - 1]
                    continue
                }
                
                // Calculamos costos según el tipo de carácter (Claude's genius)
                val involvesSibilant = a[i-1] in SIBILANT_CHARS || b[j-1] in SIBILANT_CHARS
                val cost = if (involvesSibilant) SIBILANT_COST else REGULAR_COST
                
                dp[i][j] = minOf(
                    dp[i - 1][j] + cost,      // Deletion
                    dp[i][j - 1] + cost,      // Insertion
                    dp[i - 1][j - 1] + cost   // Substitution
                )
            }
        }
        return dp[a.length][b.length]
    }
}
