package com.uriel.logpose.thamis.normalizer

import java.text.Normalizer
import com.thamis.lab.core.common.phonetic.PhoneticUtils

/**
 * THAMIS v22.23 (Acoustic Singularity) — LanguageNormalizer.
 * Motor Rioplatense Kernel & Similitud Difusa (Fuzzy DNA).
 */
object LanguageNormalizer {

    // =========================================================================
    // 1. KERNEL DE COLAPSO MORFOLÓGICO RIOPLATENSE
    // =========================================================================

    // v57.0: Regex pre-compilados (Misión #057)
    private val ARTICLES_REGEX = Regex("\\b(el|la|los|las|un|una|unos|unas|de|del)\\b")
    private val SPACES_REGEX = Regex("\\s+")

    /**
     * Proceso de Colapso Rioplatense (The Kernel).
     * Transforma cualquier cadena escuchada a ADN fonético puro.
     */
    fun normalizeToDNA(text: String): String {
        return PhoneticUtils.normalizeToDNA(text)
    }

    /**
     * Alias directo para compatibilidad con el pipeline principal.
     */
    fun normalize(text: String): String = normalizeToDNA(text)

    // =========================================================================
    // 2. ALGORITMO DE LEVENSHTEIN Y SIMILITUD DIFUSA (FUZZY DNA)
    // =========================================================================

    /**
     * Calcula la Similitud Difusa usando Distancia de Levenshtein (Margen de error 15%).
     * @return Valor flotante entre 0.0 (diferente) y 1.0 (idéntico).
     */
    fun getFuzzySimilarity(input: String, target: String): Float {
        val adnInput = normalizeToDNA(input)
        val adnTarget = normalizeToDNA(target)

        if (adnInput == adnTarget) return 1.0f

        val distance = levenshtein(adnInput, adnTarget)
        val maxLen = maxOf(adnInput.length, adnTarget.length)

        if (maxLen == 0) return 1.0f
        return 1.0f - (distance.toFloat() / maxLen)
    }

    /**
     * Evalúa si dos cadenas superan el umbral de similitud difusa (por defecto 85%).
     */
    fun isFuzzyDnaMatch(s1: String, s2: String, threshold: Double = 0.85): Boolean {
        return getFuzzySimilarity(s1, s2) >= threshold.toFloat()
    }

    private fun levenshtein(s1: String, s2: String): Int {
        val n = s1.length
        val m = s2.length
        if (n == 0) return m
        if (m == 0) return n

        var prev = IntArray(m + 1)
        var curr = IntArray(m + 1)

        for (j in 0..m) prev[j] = j

        for (i in 1..n) {
            curr[0] = i
            for (j in 1..m) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                curr[j] = minOf(curr[j - 1] + 1, prev[j] + 1, prev[j - 1] + cost)
            }
            val temp = prev
            prev = curr
            curr = temp
        }
        return prev[m]
    }

    // =========================================================================
    // 3. FILTROS DE VERBO CRÍTICO & AGNOSTICISMO DE ARTÍCULOS
    // =========================================================================

    /**
     * Agnosticismo de Artículos ("el", "la", "un", "una", "de").
     */
    fun stripArticles(text: String): String {
        return text.replace(ARTICLES_REGEX, "")
            .replace(SPACES_REGEX, " ")
            .trim()
    }

    /**
     * Bloqueo Estricto de Intención por Verbo Crítico.
     */
    fun detectLockedVerbIntent(text: String): String? {
        val lower = text.lowercase()
        return when {
            lower.contains(Regex("\\b(pone|pone|poneme|reproduci|reproduce|reproducir|play|pasame|tira|escuchar|escucha)\\b")) -> "PLAY_MUSIC"
            lower.contains(Regex("\\b(ir a|llevame|anda a|guiame|poner gps|ruta a|navegar)\\b")) -> "NAVIGATE"
            lower.contains(Regex("\\b(llama|llamar|marcar|comunicar)\\b")) -> "CALL_CONTACT"
            lower.contains(Regex("\\b(subi|baja|pausa|detene|apaga|prende)\\b")) -> "SYSTEM_CONTROL"
            else -> null
        }
    }
}