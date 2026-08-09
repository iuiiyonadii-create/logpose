package com.uriel.logpose.thamis.normalizer

import java.text.Normalizer

/**
 * THAMIS v22.23 (Acoustic Singularity) — LanguageNormalizer.
 * Motor Rioplatense Kernel & Similitud Difusa (Fuzzy DNA).
 */
object LanguageNormalizer {

    // =========================================================================
    // 1. KERNEL DE COLAPSO MORFOLÓGICO RIOPLATENSE
    // =========================================================================

    /**
     * Proceso de Colapso Rioplatense (The Kernel).
     * Transforma cualquier cadena escuchada a ADN fonético puro.
     */
    fun normalizeToDNA(text: String): String {
        return text.lowercase()
            .replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u")
            .replace("ü", "u").replace("ñ", "n")
            // 1. Yeísmo: y/ll -> j
            .replace("ll", "j").replace("y", "j")
            // 2. Aspiración de S: Ignorar S final o antes de consonante
            .replace(Regex("s\\b"), "")
            .replace(Regex("s(?=[bcdfghjklmnpqrstvwxyz])"), "")
            // 3. Colapso de Oclusivas (v->b, z/c->s)
            .replace("v", "b").replace("z", "s").replace("c", "s")
            // 4. Limpieza final alfanumérica
            .replace(Regex("[^a-z0-9]"), "")
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
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j

        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = minOf(dp[i - 1][j] + 1, dp[i][j - 1] + 1, dp[i - 1][j - 1] + cost)
            }
        }
        return dp[s1.length][s2.length]
    }

    // =========================================================================
    // 3. FILTROS DE VERBO CRÍTICO & AGNOSTICISMO DE ARTÍCULOS
    // =========================================================================

    /**
     * Agnosticismo de Artículos ("el", "la", "un", "una", "de").
     */
    fun stripArticles(text: String): String {
        return text.replace(Regex("\\b(el|la|los|las|un|una|unos|unas|de|del)\\b"), "")
            .replace("\\s+".toRegex(), " ")
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