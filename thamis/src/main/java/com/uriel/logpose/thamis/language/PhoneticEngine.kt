package com.uriel.logpose.thamis.language

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.compat.core.AppContextProvider
import com.uriel.logpose.core.parser.PhoneticDictionary
import com.uriel.logpose.core.services.MusicNormalizerDelegator
import kotlinx.coroutines.launch
import org.json.JSONObject

/**
 * PhoneticEngine V6: Corrección de alucinaciones unificada vía ULC.
 */
object PhoneticEngine {

    private const val TAG = "PhoneticEngine"
    private val phoneticCache = androidx.collection.LruCache<String, String>(512)
    private val normalizationCache = androidx.collection.LruCache<String, NormalizationTrace>(512)

    // v57.0: Regex pre-compilados (Misión #057)
    private val SPACES_REGEX = Regex("\\s+")
    private val PLURALS_REGEX = Regex("s\\b")
    private val REPETITIONS_REGEX = Regex("([a-z])\\1+")
    private val VOWELS_AEO = Regex("[aeo]")
    private val VOWELS_IU = Regex("[iu]")

    private val dictionary: PhoneticDictionary by lazy {
        PhoneticDictionary(AppContextProvider.applicationContext)
    }

    private val MASTER_CORRECTIONS: Map<String, String> by lazy {
        val all = mutableMapOf<String, String>()
        all.putAll(dictionary.mapaDe("alucinaciones"))
        all.putAll(dictionary.mapaDe("modismos"))
        all.putAll(dictionary.mapaDe("musica.correcciones_foneticas"))
        all
    }

    private val dynamicBias = mutableMapOf<String, String>()

    /**
     * v11.0 STAFF: Sincronización remota ELIMINADA para evitar timeouts en calle.
     * LogPose ahora opera 100% en modo Radio-Silencio (Offline-First).
     */
    fun syncWithLab() {
        LogPoseLogger.d(TAG, "🧠 PSP: Modo local puro activado. Ignorando red.")
    }

    /**
     * Inyecta conocimiento aprendido por THAMIS LAB (PSP).
     */
    fun updatePersonalProfile(updates: Map<String, String>) {
        dynamicBias.putAll(updates)
        LogPoseLogger.i(TAG, "🧠 PSP: Perfil actualizado con ${updates.size} nuevas correcciones.")
    }

    data class NormalizationTrace(
        val raw: String,
        val musicNormalized: String,
        val pspCorrected: String?,
        val glosarioCorrected: String?,
        val finalResult: String
    )

    fun normalizeWithTrace(raw: String): NormalizationTrace {
        if (raw.isBlank()) return NormalizationTrace(raw, "", null, null, "")
        
        normalizationCache[raw]?.let { return it }

        val tStart = System.currentTimeMillis()
        val musicNorm = MusicNormalizerDelegator.normalize(raw.lowercase().trim())
        var pspResult: String? = null
        var glosarioResult: String? = null

        // 1. Match dinámico completo (Prioridad PSP)
        dynamicBias[musicNorm]?.let { 
            pspResult = it 
        }

        // 2. Match estático completo (Glosario)
        if (pspResult == null) {
            MASTER_CORRECTIONS[musicNorm]?.let { glosarioResult = it }
        }

        val result = pspResult ?: glosarioResult ?: run {
            val tokens = musicNorm.split(SPACES_REGEX)
            val sb = StringBuilder()
            
            // v6.3: Búsqueda de Ventana Deslizante para sub-frases aprendidas (Misión #024)
            var i = 0
            while (i < tokens.size) {
                var foundMatch = false
                // Intentamos match de hasta 4 tokens (e.g. "alicia moreau de justo")
                for (len in 4 downTo 1) {
                    if (i + len <= tokens.size) {
                        val subPhrase = tokens.subList(i, i + len).joinToString(" ")
                        val corrected = com.uriel.logpose.thamis.learning.LearningEngine.getPhoneticCorrection(subPhrase) 
                            ?: MASTER_CORRECTIONS[subPhrase]
                        
                        if (corrected != null) {
                            if (sb.isNotEmpty()) sb.append(" ")
                            sb.append(corrected)
                            i += len
                            foundMatch = true
                            break
                        }
                    }
                }
                
                if (!foundMatch) {
                    val token = tokens[i]
                    if (token.length >= 2 || token in setOf("a", "y", "o")) {
                        if (sb.isNotEmpty()) sb.append(" ")
                        sb.append(token)
                    }
                    i++
                }
            }
            sb.toString()
        }

        val finalTrace = NormalizationTrace(
            raw = raw,
            musicNormalized = musicNorm,
            pspCorrected = pspResult,
            glosarioCorrected = glosarioResult,
            finalResult = result
        )
        
        normalizationCache.put(raw, finalTrace)
        return finalTrace
    }

    private fun logPSPAudit(action: String, token: String, duration: Long) {
        val audit = JSONObject().apply {
            put("type", "PSP_AUDIT")
            put("action", action)
            put("token", token)
            put("duration_ms", duration)
        }
        // Enviar vía UDP al lab
        com.uriel.logpose.thamis.cognitive.CognitivePipeline.sendTelemetryProxy(audit)
    }

    fun normalize(raw: String): String = normalizeWithTrace(raw).finalResult

    /**
     * Algoritmo de Llave Fonética Rioplatense (ALF-R) v4.0 "Contextual Deep Match".
     * Soporta colapso de rimas asonantes agresivo (Fuzzy Collapse Pro) y
     * normalización de estrés vocálico para voseo adaptativo.
     */
    fun getPhoneticKey(text: String, noiseLevel: Float = 0.0f): String {
        if (text.isEmpty()) return ""
        
        val isExtremeNoise = noiseLevel > 0.8f
        val cacheKey = if (isExtremeNoise) "v4_n_$text" else "v4_$text"
        
        phoneticCache[cacheKey]?.let { return it }

        var key = text.lowercase().trim()
            .replace("á", "a").replace("é", "e").replace("í", "i")
            .replace("ó", "o").replace("ú", "u").replace("ü", "u")

        key = key.replace("y", "i")
            .replace("ll", "i")
            .replace("sh", "i")
            .replace("ch", "i")
            .replace("v", "b")
            .replace("h", "")
            .replace("z", "s")
            .replace("c", "k")
            .replace("q", "k")
            .replace("j", "h")
            .replace("g", "h")
            .replace(PLURALS_REGEX, "") 

        key = key.replace("p", "b")
            .replace("t", "d")
            .replace("f", "b")
            
        key = key.replace(REPETITIONS_REGEX, "$1")

        if (isExtremeNoise) {
            key = key.replace(VOWELS_AEO, "V")
            key = key.replace(VOWELS_IU, "I")
        }

        phoneticCache.put(cacheKey, key)
        return key
    }

    /**
     * Calcula la similitud entre dos strings basada en sus llaves fonéticas ALF-R v4.5.
     * Implementa Token-Level Matching para evitar que palabras clave se diluyan en el promedio.
     */
    fun similarity(
        s1: String, 
        s2: String, 
        noiseLevel: Float = 0.0f,
        semanticCloud: Map<String, Float> = emptyMap()
    ): Float {
        if (s1 == s2) return 1.0f
        
        val k1 = s1.split(" ").map { getPhoneticKey(it, noiseLevel) }.filter { it.isNotEmpty() }
        val k2 = s2.split(" ").map { getPhoneticKey(it, noiseLevel) }.filter { it.isNotEmpty() }
        
        if (k1.isEmpty() || k2.isEmpty()) return 0f

        // --- ALF-R v4.5: Anchor Matching Logic ---
        var matchedTokens = 0
        for (targetToken in k2) {
            // Buscamos si el token objetivo está presente en el input
            if (k1.any { it == targetToken }) {
                matchedTokens++
            } else {
                // Fallback: Similitud difusa por token si no hay match exacto
                if (k1.any { levenshtein(it, targetToken).toFloat() / maxOf(it.length, targetToken.length) < 0.3f }) {
                    matchedTokens++
                }
            }
        }
        
        var score = matchedTokens.toFloat() / k2.size.toFloat()

        // --- Semantic Boost ---
        // Aplicamos boost si el s2 (original) tiene palabras en la nube semántica
        val originalTokens = s2.lowercase().split(" ")
        for (token in originalTokens) {
            semanticCloud[token]?.let { boost ->
                score = (score + boost).coerceAtMost(1.0f)
                LogPoseLogger.d(TAG, "🧠 Semantic Boost (v4.5) aplicado a '$token': +$boost")
            }
        }

        return score
    }

    /**
     * Versión optimizada para el índice fonético.
     */
    fun similarityWithKeys(
        k1: String, // Input completo ya procesado
        k2: String, // Target ya procesado
        originalTarget: String,
        semanticCloud: Map<String, Float> = emptyMap()
    ): Float {
        // En esta versión, tratamos las llaves como bloques únicos para velocidad de índice
        if (k1 == k2) return 1.0f
        
        val dist = levenshtein(k1, k2)
        val maxLen = maxOf(k1.length, k2.length)
        var score = 1.0f - (dist.toFloat() / maxLen.toFloat())
        
        semanticCloud[originalTarget.lowercase()]?.let { boost ->
            score = (score + boost).coerceAtMost(1.0f)
        }
        return score
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
