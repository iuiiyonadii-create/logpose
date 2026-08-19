package com.uriel.logpose.core.parser

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * TranscriptSanityGuard: Filtro preventivo para evitar el procesamiento de basura repetitiva.
 * Ataca de raíz el ANR causado por Levenshtein sobre strings gigantes generados por ruido.
 */
object TranscriptSanityGuard {

    private const val MAX_WORDS = 10
    private const val MAX_WORD_REPETITION = 3
    private const val MAX_BIGRAM_REPETITION = 2

    sealed class Verdict {
        object Accept : Verdict()
        data class Reject(val reason: String) : Verdict()
    }

    fun evaluate(transcript: String): Verdict {
        val words = transcript.trim().split(Regex("\\s+")).filter { it.isNotBlank() }

        if (words.isEmpty()) return Verdict.Reject("vacío")

        if (words.size > MAX_WORDS) {
            return Verdict.Reject("demasiado largo (${words.size} palabras)")
        }

        // 1. Chequeo de repetición de palabras simples
        val wordCounts = words.groupingBy { it.lowercase() }.eachCount()
        val maxWordCount = wordCounts.values.maxOrNull() ?: 0
        if (maxWordCount > MAX_WORD_REPETITION) {
            return Verdict.Reject("palabra repetida $maxWordCount veces")
        }

        // 2. Chequeo de repetición de bigramas (ej: "numero uno numero uno")
        if (words.size >= 4) {
            val bigrams = words.zipWithNext { a, b -> "${a.lowercase()} ${b.lowercase()}" }
            val bigramCounts = bigrams.groupingBy { it }.eachCount()
            val maxBigramCount = bigramCounts.values.maxOrNull() ?: 0
            if (maxBigramCount > MAX_BIGRAM_REPETITION) {
                return Verdict.Reject("bigrama repetido $maxBigramCount veces")
            }
        }

        return Verdict.Accept
    }
}
