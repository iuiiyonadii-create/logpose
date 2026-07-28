package com.uriel.logpose.thamis.language

import android.util.Log
import com.uriel.logpose.features.voice.MusicVocabulary

/**
 * PhoneticEngine V5: Corrección de alucinaciones unificada.
 * Ahora incluye métodos de similitud y llaves fonéticas para el resto del sistema.
 */
object PhoneticEngine {

    private const val TAG = "PhoneticEngine"
    private val phoneticCache = mutableMapOf<String, String>()

    private val CASCO_ALUCINATIONS = mapOf(
        "one thought" to "duki",
        "adult" to "duki",
        "one duck" to "duki",
        "one x t" to "ysy a",
        "obe quitan" to "ysy a",
        "abrigos sapo" to "whatsapp",
        "sapo" to "whatsapp",
        "rocks" to "rockstar",
        "rock" to "rockstar",
        "duque" to "duki",
        "doce" to "duki",
        "donde" to "duki",
        "isi" to "ysy a",
        "izi" to "ysy a",
        "esaoy" to "estoy",
        "estoy" to "donde", 
        "voz" to "wos",
        "vos" to "wos",
        "vo" to "wos",
        "conquistan" to "",
        "log pose" to "",
        "lock pose" to "",
        "ok" to "",
        "okay" to "",
        "musica" to "",
        "música" to "",
        "che" to "",
        "boludo" to "",
        "escuchame" to "",
        "escucháme" to ""
    )

    fun normalize(raw: String): String {
        if (raw.isBlank()) return ""
        val normalized = MusicVocabulary.normalize(raw.lowercase().trim())

        // 1. Match completo (Prioridad máxima)
        CASCO_ALUCINATIONS[normalized]?.let { return it }

        // 2. Por tokens (Optimizado con StringBuilder)
        val tokens = normalized.split(Regex("\\s+"))
        val result = StringBuilder()
        
        for (token in tokens) {
            val corrected = CASCO_ALUCINATIONS[token] ?: token
            if (corrected.isNotBlank()) {
                if (result.isNotEmpty()) result.append(" ")
                result.append(corrected)
            }
        }
        
        return result.toString()
    }

    /**
     * Algoritmo de Llave Fonética Rioplatense (ALF-R).
     * Requerido por SimilarityEngine.
     */
    fun getPhoneticKey(text: String): String {
        if (text.isEmpty()) return ""
        val cached = phoneticCache[text]
        if (cached != null) return cached

        var key = text.lowercase().trim()
        
        key = key.replace("ll", "y")
            .replace("sh", "y")
            .replace("ch", "y")
            .replace("z", "s")
            .replace("ce", "se")
            .replace("ci", "si")
            .replace("v", "b")
            .replace("h", "")
            
        key = key.replace("qu", "k")
            .replace("q", "k")
            .replace("c", "k")
            .replace("x", "ks")
            
        key = key.replace("j", "h")
            .replace("ge", "he")
            .replace("gi", "hi")
            .replace(Regex("s\\b"), "")

        key = key.replace(Regex("([a-z])\\1+"), "$1")

        phoneticCache[text] = key
        return key
    }

    /**
     * Calcula la similitud entre dos strings basada en sus llaves fonéticas.
     * Requerido por VoiceManager e IntentDetector.
     */
    fun similarity(s1: String, s2: String): Float {
        if (s1 == s2) return 1.0f
        
        val k1 = getPhoneticKey(s1)
        val k2 = getPhoneticKey(s2)
        
        if (k1 == k2) return 1.0f
        if (k1.isEmpty() || k2.isEmpty()) return 0f

        val dist = levenshtein(k1, k2)
        val maxLen = maxOf(k1.length, k2.length)
        
        return 1.0f - (dist.toFloat() / maxLen.toFloat())
    }

    private fun levenshtein(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j
        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        return dp[s1.length][s2.length]
    }
}
