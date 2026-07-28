package com.uriel.logpose.thamis.language.advanced

import kotlin.math.max

/**
 * Matcher basado en N-Gramas (Trigramas por defecto).
 * Muy resistente al ruido y a la pérdida de caracteres en el stream de audio.
 */
object FuzzyNGramMatcher {

    fun score(a: String, b: String, n: Int = 3): Float {
        if (a == b) return 1.0f
        if (a.length < n || b.length < n) {
            // Fallback a Jaro-Winkler si son muy cortos
            return 0f 
        }

        val gramsA = getNGrams(a, n)
        val gramsB = getNGrams(b, n)

        val intersection = gramsA.intersect(gramsB).size
        val union = (gramsA + gramsB).distinct().size

        return intersection.toFloat() / union.toFloat()
    }

    private fun getNGrams(text: String, n: Int): List<String> {
        val nGrams = mutableListOf<String>()
        val cleanText = text.lowercase().replace(Regex("\\s+"), "_")
        for (i in 0..cleanText.length - n) {
            nGrams.add(cleanText.substring(i, i + n))
        }
        return nGrams
    }
}
